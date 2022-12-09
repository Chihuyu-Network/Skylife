package love.chihuyu.skylife

import love.chihuyu.skylife.barter.BarterCommand
import love.chihuyu.skylife.barter.BarterEvent
import love.chihuyu.skylife.data.ItemDataManager
import love.chihuyu.skylife.database.GachaStorages
import love.chihuyu.skylife.database.Stats
import love.chihuyu.skylife.database.Users
import love.chihuyu.skylife.gacha.GachaEvent
import love.chihuyu.skylife.gacha.GachaGiveCommand
import love.chihuyu.skylife.gacha.GachaShopCommand
import love.chihuyu.skylife.gacha.GachaShopEvent
import love.chihuyu.skylife.gachastorage.GachaStorageCommand
import love.chihuyu.skylife.gachastorage.GachaStorageEvent
import love.chihuyu.skylife.scoreboard.Scoreboard
import love.chihuyu.skylife.util.MEOW
import net.md_5.bungee.api.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class Skylife : JavaPlugin(), Listener {

    companion object {
        lateinit var plugin: JavaPlugin
    }

    init {
        plugin = this
    }

    override fun onEnable() {
        val enabledCommands =
            setOf(BarterCommand, GachaGiveCommand, GachaShopCommand, GachaStorageCommand)
        val enabledEvents = setOf(this, BarterEvent, GachaEvent, GachaShopEvent, GachaStorageEvent)
        enabledCommands.forEach { it.register() }
        enabledEvents.forEach { server.pluginManager.registerEvents(it, this) }
        logger.info("This Plug has Enabled")

        ItemDataManager.checkDuplicate()
        logger.info("Debug Code has Ran")

        if (!plugin.dataFolder.exists()) {
            config.options().copyDefaults(true)
            saveResource("config.yml", false)
        }

        val enabledDatabase = arrayOf(Users, Stats, GachaStorages)
        Database.connect("jdbc:sqlite:${plugin.dataFolder}/skylife.db", "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(*enabledDatabase)
            SchemaUtils.createMissingTablesAndColumns(*enabledDatabase)
        }
        logger.info("This Plugin has Loaded")
    }

    override fun onDisable() {
        logger.info("This Plugin has Disabled")
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        var joinMessage = "${ChatColor.YELLOW}${player.name} joined the game"

        if (!player.hasPlayedBefore()) {
            joinMessage += "${ChatColor.LIGHT_PURPLE} (First Join)"
            player.gameMode = GameMode.SURVIVAL
            player.bedSpawnLocation = Location(player.world, 0.0, 64.0, 0.0)

            player.teleport(Location(player.world, 0.0, 64.0, 0.0))
            player.sendTitle("${ChatColor.GOLD}-= Welcome to Skylife =-", "", 10, 70, 20)
        }

        transaction {
            // FIXME: このままでは、テーブル:キーを増やしたときに database not found になる
            if (!Users.select { Users.uuid eq player.uniqueId }.empty()) return@transaction
            Users.insert {
                it[uuid] = player.uniqueId
                it[statsId] = Stats.insert {} get Stats.id
                it[gachaStorageId] = GachaStorages.insert {} get GachaStorages.id
            }
        }

        event.joinMessage = joinMessage
        Scoreboard.update(player)
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.player.world.playSound(event.player.location, Sound.BLOCK_WOODEN_DOOR_CLOSE, 1f, 1f)
    }
}

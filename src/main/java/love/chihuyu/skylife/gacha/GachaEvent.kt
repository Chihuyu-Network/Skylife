package love.chihuyu.skylife.gacha

import love.chihuyu.skylife.data.GachaData
import love.chihuyu.skylife.database.GachaStorages
import love.chihuyu.skylife.database.Stats
import love.chihuyu.skylife.database.Users
import love.chihuyu.skylife.scoreboard.Scoreboard
import love.chihuyu.skylife.util.*
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object GachaEvent : Listener {

    private fun lore(userName: String) = listOf(
        "所有者: $userName"
    )

    private fun nyamazon(gachaName: String) =
        "${ChatColor.LIGHT_PURPLE}[Nyamazon]${ChatColor.RESET} ${gachaName}ガチャをお届けしました。"

    // OPTIMIZE: なんとなくだけど DB とのやり取りが多くて辛そう。
    // - 愚直に考えると、onJoin, onQuit, onDisable イベントでだけ DB の読み書きをしたら良い気がする。
    // - onEnable で DB を全部読み込んでオブジェクトにするのは起動が遅くなりそうだし、無駄が多いとも思う。
    // - onJoin で ResultRaw からオブジェクト化、onQuit, onDisable で Table#update かなぁ。
    @EventHandler
    fun onItemConsume(event: PlayerItemConsumeEvent) {
        val player = event.player

        background {
            newSuspendedTransaction {
                val row = (Users innerJoin Stats)
                    .slice(Users.statsId, Users.gachaStorageId, Stats.foodConsumed)
                    .select { Users.uuid eq player.uniqueId }.single()

                Stats.increment(Stats.foodConsumed) { Stats.id eq row[Users.statsId] }

                if ((row[Stats.foodConsumed] + 1) % 32 != 0) return@newSuspendedTransaction

                GachaStorages.increment(GachaStorages.shokuryo) { GachaStorages.id eq row[Users.gachaStorageId] }
                player.playSound(player.location, MEOW, 1f, 1f)
                player.sendRawMessage(nyamazon("食料"))
            }
        }

        Scoreboard.update(player)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        background {
            newSuspendedTransaction {
                val row = (Users innerJoin Stats innerJoin GachaStorages)
                    .slice(Users.statsId, Users.gachaStorageId, Stats.blockPlaced)
                    .select { Users.uuid eq player.uniqueId }.single()

                Stats.increment(Stats.blockPlaced) { Stats.id eq row[Users.statsId] }

                if ((row[Stats.blockPlaced] + 1) % 64 != 0) return@newSuspendedTransaction

                GachaStorages.increment(GachaStorages.kenzai) { GachaStorages.id eq row[Users.gachaStorageId] }
                player.playSound(player.location, MEOW, 1f, 1f)
                player.sendRawMessage(nyamazon("建材"))
            }
        }

        Scoreboard.update(player)
    }

    @EventHandler
    fun onToolBroken(event: PlayerItemBreakEvent) {
        val player = event.player

        background {
            newSuspendedTransaction {
                val row = Users.slice(Users.statsId, Users.gachaStorageId)
                    .select { Users.uuid eq player.uniqueId }.single()

                Stats.increment(Stats.toolBroken) { Stats.id eq row[Users.statsId] }
                GachaStorages.increment(GachaStorages.kinrokansha) { GachaStorages.id eq row[Users.gachaStorageId] }
            }
            player.playSound(player.location, Sound.ENTITY_CAT_AMBIENT, 1f, 1f)
            player.sendRawMessage(nyamazon("勤労感謝"))
            Scoreboard.update(player)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action !in listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)) return
        if (event.clickedBlock?.type?.isInteractable == true) return
        if (!event.hasItem()) return
        val usedItem = event.item ?: return
        val gacha = GachaData.buyables[usedItem.getCustomModelDataOrNull()] ?: return

        event.isCancelled = true

        val player = event.player
        // TODO: GachaData に入れる
        val chooseTimes = when (gacha.customModelData) {
            5040 -> 4
            else -> 1
        }
        val dropCount = when (gacha.customModelData) {
            5040 -> 16
            else -> 1
        }

        // OPTIMIZE: なんとなく無駄がある気がする
        val gachaTimes = if (player.isSneaking) usedItem.amount else 1
        val dropItems = mutableListOf<ItemStack>()
        repeat(gachaTimes) {
            repeat(chooseTimes) {
                val dropItem = ItemUtil.create(
                    gacha.chooseMaterial(),
                    count = dropCount,
                    lore = lore(player.displayName)
                )
                dropItems.add(dropItem)
            }
        }
        usedItem.amount -= gachaTimes
        player.inventory.addOrDropItem(*dropItems.toTypedArray())
        player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
    }
}

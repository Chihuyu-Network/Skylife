package love.chihuyu.skylife.scoreboard

import love.chihuyu.skylife.Skylife.Companion.plugin
import love.chihuyu.skylife.database.Stats
import love.chihuyu.skylife.database.Users
import love.chihuyu.skylife.util.background
import net.md_5.bungee.api.ChatColor.*
import org.bukkit.Bukkit.getScoreboardManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Scoreboard {
    fun update(player: Player) {
        val name = "skylife_stats_${player.uniqueId}"
        val displayName = "${ChatColor.AQUA}${ChatColor.BOLD}${ChatColor.ITALIC}SKYLIFE ${ChatColor.GRAY}v0.1"
        val manager = getScoreboardManager() ?: return
        val scoreboard = manager.newScoreboard
        val objective = scoreboard.getObjective(name)
            ?: scoreboard.registerNewObjective(name, "", displayName)

        val perSyokuryoGacha = 32
        val perKenzaiGacha = 64
        background {
            newSuspendedTransaction {
                val foodConsumed = (Users innerJoin Stats).slice(Stats.foodConsumed)
                    .select { Users.uuid eq player.uniqueId }
                    .map { it[Stats.foodConsumed] }.first()

                val blockPlaced = (Users innerJoin Stats).slice(Stats.blockPlaced)
                    .select { Users.uuid eq player.uniqueId }
                    .map { it[Stats.blockPlaced] }.first()
                val nextSyokuryoGacha = perSyokuryoGacha - (foodConsumed % perSyokuryoGacha)
                val nextKenzaiGacha = perKenzaiGacha - (blockPlaced % perKenzaiGacha)
                objective.apply {
                    getScore("$GOLD$STRIKETHROUGH━━━━━━━━━━━━━━━━━━━$RESET").score = 8
                    getScore("   ").score = 7
                    getScore("プレイヤー数: ${plugin.server.onlinePlayers.size}").score = 6
                    getScore("  ").score = 5
                    getScore("次のガチャまで").score = 4
                    getScore("食 $GRAY> $WHITE$nextSyokuryoGacha${GRAY}/32").score = 3
                    getScore("建 $GRAY> $WHITE$nextKenzaiGacha${GRAY}/64").score = 2
                    getScore(" ").score = 1
                    getScore("$GOLD$STRIKETHROUGH━━━━━━━━━━━━━━━━━━━").score = 0
                    displaySlot = DisplaySlot.SIDEBAR
                }
            }
            player.scoreboard = scoreboard
        }
    }
}

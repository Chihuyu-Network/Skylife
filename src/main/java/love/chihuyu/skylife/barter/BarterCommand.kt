package love.chihuyu.skylife.barter

import love.chihuyu.skylife.base.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BarterCommand : Command("barter") {
    override fun onCommand(sender: CommandSender, label: String, args: Array<out String>) {
        if (sender !is Player) return
        if (sender.hasPermission("skylife.command.barter")) BarterGui.open(sender)
    }

    override fun onTabComplete(sender: CommandSender, label: String, args: Array<out String>) =
        listOf<String>()
}

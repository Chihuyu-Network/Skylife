package love.chihuyu.skylife.gachastorage

import love.chihuyu.skylife.base.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GachaStorageCommand : Command("gachastorage") {
    override fun onCommand(sender: CommandSender, label: String, args: Array<out String>) {
        if (sender !is Player) return
        if (sender.hasPermission("skylife.command.gachastorage")) GachaStorageGui.open(sender)
    }

    override fun onTabComplete(sender: CommandSender, label: String, args: Array<out String>) =
        listOf<String>()
}

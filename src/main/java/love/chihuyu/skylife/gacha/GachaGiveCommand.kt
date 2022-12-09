package love.chihuyu.skylife.gacha

import love.chihuyu.skylife.base.Command
import love.chihuyu.skylife.data.GachaData
import love.chihuyu.skylife.util.addOrDropItem
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GachaGiveCommand : Command("gachagive") {
    override fun onCommand(sender: CommandSender, label: String, args: Array<out String>) {
        if (sender !is Player) return
        if (!sender.hasPermission("skylife.command.gachagive")) {
            sender.sendRawMessage("このコマンドを実行する権限がありません。")
            return
        }

        when (args.size) {
            1 -> "ターゲットを指定してください。"
            2 -> "ガチャ券を指定してください。"
            else -> null
        }?.let { sender.sendRawMessage(it); return }

        val target = sender.server.onlinePlayers.find { it.displayName == args[0] }
        val gacha = GachaData.data[args[1]]
        when {
            target == null -> "ターゲットが見つかりませんでした。"
            gacha == null -> "ガチャ券が見つかりませんでした。"
            else -> null
        }?.let { sender.sendRawMessage(it); return }

        val amount = args.getOrNull(2)?.toIntOrNull() ?: 1
        target!!.inventory.addOrDropItem(gacha!!.getItemStack(amount))
    }

    override fun onTabComplete(
        sender: CommandSender,
        label: String,
        args: Array<out String>
    ): List<String> {
        val playerNames = sender.server.onlinePlayers.toTypedArray().map { it.displayName }
        val gachaNames = GachaData.data.keys.toList()
        val amounts = listOf("1", "16", "32", "64")

        return when (args.size) {
            1 -> playerNames
            2 -> gachaNames
            3 -> amounts
            else -> listOf()
        }.filter { it.contains(args[args.size - 1]) }
    }
}

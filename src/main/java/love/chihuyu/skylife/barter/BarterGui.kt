package love.chihuyu.skylife.barter

import love.chihuyu.skylife.barter.constants.Areas
import love.chihuyu.skylife.barter.constants.Panels
import love.chihuyu.skylife.data.ItemDataManager
import love.chihuyu.skylife.util.ItemUtil
import org.bukkit.Bukkit.createInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object BarterGui {
    val page = hashMapOf<Player, Int>()
    val gui = hashMapOf<Player, Inventory>()

    fun create() = createInventory(null, 54, "Barter")
        .apply {
            Areas.tradable.forEach { setItem(it, Panels.fill) }
            Areas.separator.forEach { setItem(it, Panels.separate) }
            setItem(38, Panels.prevPage)
            setItem(47, Panels.nextPage)
        }

    fun open(player: Player) {
        if (!gui.containsKey(player)) gui[player] = create()

        page[player] = 0
        player.openInventory(gui[player]!!)
    }

    fun update(player: Player) {
        val barterInv = gui[player]!!
        val tradingItems = Areas.trading.mapNotNull(barterInv::getItem).map { it.type }
        val tradableItems = ItemDataManager.getTradableItems(*tradingItems.toTypedArray())
        val chunkedTradableItems =
            tradableItems.chunked(36).getOrNull(page[player]!!) ?: listOf()
        val tradableLore = listOf(
            "左クリック ->  1個",
            "シフト＋左 -> 64個",
            "右クリック -> 32個",
            "シフト＋右 -> 16個"
        )

        Areas.tradable.forEachIndexed { i, slot ->
            val item = chunkedTradableItems.getOrNull(i)
                ?.let { ItemUtil.create(it, lore = tradableLore) }
                ?: Panels.fill
            barterInv.setItem(slot, item)
        }
    }
}

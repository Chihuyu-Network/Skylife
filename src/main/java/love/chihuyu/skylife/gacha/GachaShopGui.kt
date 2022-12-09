package love.chihuyu.skylife.gacha

import love.chihuyu.skylife.barter.constants.Panels
import love.chihuyu.skylife.data.GachaData
import org.bukkit.Bukkit.createInventory
import org.bukkit.entity.Player

object GachaShopGui {
    val gachaShopGui = createInventory(null, 27, "GachaShop")
        .apply {
            repeat(size) { setItem(it, Panels.fill) }
            GachaData.buyables.values.mapNotNull { it.shopData }
                .forEach { setItem(it.slot, it.getItemStack(1)) }
        }

    fun open(player: Player) {
        player.openInventory(gachaShopGui)
    }
}

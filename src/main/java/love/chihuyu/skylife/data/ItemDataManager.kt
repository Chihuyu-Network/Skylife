package love.chihuyu.skylife.data

import love.chihuyu.skylife.Skylife.Companion.plugin
import love.chihuyu.skylife.util.AnsiColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object ItemDataManager {
    fun isTradable(material: Material) = ItemData.tradableList.any { it.contains(material) }
    fun isTradable(item: ItemStack) = isTradable(item.type)

    fun getTradableItems(vararg materials: Material) =
        ItemData.tradableList.filter { it.any { v -> v in materials } }.flatten()

    fun checkDuplicate() {
        val flattenData = ItemData.tradableList.flatten()
        val duplicated = flattenData.filter { flattenData.minus(it).contains(it) }
        if (duplicated.isEmpty()) return

        plugin.logger.warning("${AnsiColor.YELLOW}These items are duplicated!\n$duplicated${AnsiColor.RESET}")
    }
}

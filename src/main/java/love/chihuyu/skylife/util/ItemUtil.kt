package love.chihuyu.skylife.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object ItemUtil {

    fun create(
        material: Material,
        name: String? = null,
        count: Int = 1,
        unbreakable: Boolean = false,
        lore: List<String> = listOf(),
        customModelData: Int? = null
    ): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta

        meta?.isUnbreakable = unbreakable
        meta?.setDisplayName(name)
        meta?.setCustomModelData(customModelData)
        meta?.lore = lore
        item.amount = count
        item.itemMeta = meta

        return item
    }
}

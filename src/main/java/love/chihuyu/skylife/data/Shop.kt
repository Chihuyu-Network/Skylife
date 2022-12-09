package love.chihuyu.skylife.data

import love.chihuyu.skylife.base.CustomItem
import love.chihuyu.skylife.util.ItemUtil
import org.bukkit.Material

data class Shop(
    override val material: Material,
    override val name: String,
    val slot: Int,
    val customModelData: Int,
    val price: Pair<Material, Int>,
    val additionalLore: List<String> = listOf()
) : CustomItem {
    private val lore = listOf(
        "${price.first} x${price.second} で交換できます。",
        *if (additionalLore.isEmpty()) arrayOf() else arrayOf(""),
        *additionalLore.toTypedArray()
    )

    override fun getItemStack(amount: Int) =
        ItemUtil.create(material, name, amount, false, lore, customModelData)
}

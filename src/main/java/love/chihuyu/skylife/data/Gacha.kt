package love.chihuyu.skylife.data

import love.chihuyu.skylife.base.CustomItem
import love.chihuyu.skylife.util.ItemUtil
import love.chihuyu.skylife.util.Randomizer
import love.chihuyu.skylife.util.setFlags
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag

data class Gacha(
    override val material: Material,
    override val name: String,
    val lore: List<String>,
    val customModelData: Int,
    val slot: Int?,
    val price: Pair<Material, Int>?,
    val randomizer: Randomizer<Material>
) : CustomItem {
    val shopData =
        if (price == null || slot == null) null
        else Shop(material, name, slot, customModelData + 5000, price)

    override fun getItemStack(amount: Int) = ItemUtil
        .create(material, name, amount, true, lore, customModelData)
        .apply {
            addUnsafeEnchantment(Enchantment.MENDING, 0)
            setFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
        }

    fun chooseMaterial() = randomizer.choose()
}

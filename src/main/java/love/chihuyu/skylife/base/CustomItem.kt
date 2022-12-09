package love.chihuyu.skylife.base

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface CustomItem {
    val material: Material
    val name: String
    fun getItemStack(amount: Int): ItemStack
}

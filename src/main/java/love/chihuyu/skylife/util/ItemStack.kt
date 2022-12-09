package love.chihuyu.skylife.util

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

fun ItemStack.setFlags(vararg flags: ItemFlag) {
    val meta = this.itemMeta
    meta?.addItemFlags(*flags)
    this.itemMeta = meta
}

fun ItemStack.getItemMetaOrNull() = if (hasItemMeta()) itemMeta!! else null
fun ItemStack.getCustomModelDataOrNull() =
    if (hasItemMeta() && itemMeta?.hasCustomModelData() == true) itemMeta!!.customModelData else null

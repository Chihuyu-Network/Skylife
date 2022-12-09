package love.chihuyu.skylife.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.dropItemThere(vararg items: ItemStack) =
    items.forEach { if (it.amount != 0) this.world.dropItemNaturally(this.location, it) }

package love.chihuyu.skylife.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

fun PlayerInventory.addOrDropItem(vararg items: ItemStack) =
    this.addItem(*items).values.forEach((this.holder as Player)::dropItemThere)

fun Inventory.removeAsPossible(
    desiredAmount: Int,
    isToBeRemoved: (Int, ItemStack) -> Boolean
): Int {
    val matchingItems = this.filterIndexed { index, item ->
        if (item == null) false else isToBeRemoved(index, item)
    }
    var countLeft = desiredAmount
    for (item in matchingItems) {
        val minus = countLeft.coerceAtMost(item.amount)
        item.amount -= minus
        countLeft -= minus
        if (countLeft == 0) break
    }

    return desiredAmount - countLeft
}

fun Inventory.removeAsPossible(desiredAmount: Int, isToBeRemoved: (ItemStack) -> Boolean): Int {
    return this.removeAsPossible(desiredAmount) { _, item -> isToBeRemoved(item) }
}

fun Inventory.removeAsPossible(desiredAmount: Int, type: Material): Int {
    return this.removeAsPossible(desiredAmount) { _, item -> item.type == type }
}

fun Inventory.moveTo(to: Inventory, slot: Int) {
    val clone = this.getItem(slot)?.clone() ?: return

    this.setItem(slot, ItemUtil.create(Material.AIR))
    to.addItem(clone).forEach { this.addItem(it.value) }
}

fun Inventory.moveOrDropTo(to: PlayerInventory, slot: Int) {
    val clone = this.getItem(slot)?.clone() ?: return

    this.setItem(slot, ItemUtil.create(Material.AIR))
    to.addOrDropItem(clone)
}

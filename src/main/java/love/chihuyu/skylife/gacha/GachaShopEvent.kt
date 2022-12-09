package love.chihuyu.skylife.gacha

import love.chihuyu.skylife.data.GachaData
import love.chihuyu.skylife.util.MEOW
import love.chihuyu.skylife.util.addOrDropItem
import love.chihuyu.skylife.util.getCustomModelDataOrNull
import love.chihuyu.skylife.util.removeAsPossible
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.PlayerInventory

object GachaShopEvent : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.view.topInventory != GachaShopGui.gachaShopGui) return

        event.isCancelled = true

        val clickedInv = event.clickedInventory ?: return
        val playerInv = event.view.bottomInventory as PlayerInventory
        if (clickedInv.type == playerInv.type) return
        val clickedItem = clickedInv.getItem(event.slot) ?: return
        val boughtItemData = GachaData.buyables[clickedItem.getCustomModelDataOrNull()] ?: return

        val player = event.whoClicked as Player
        val amount = when (event.click) {
            ClickType.SHIFT_LEFT -> 64
            ClickType.SHIFT_RIGHT -> 16
            ClickType.LEFT -> 1
            ClickType.RIGHT -> 32
            else -> 0
        }
        val price = boughtItemData.shopData!!.price
        val removedCount = playerInv.removeAsPossible(amount * price.second, price.first)
        val sound = if (removedCount == 0) MEOW else Sound.ENTITY_EXPERIENCE_ORB_PICKUP

        playerInv.addOrDropItem(boughtItemData.getItemStack(removedCount))
        player.playSound(player.location, sound, 0.8f, 1f)
    }

    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        if (event.view.topInventory != GachaShopGui.gachaShopGui) return

        val player = event.player as Player
        player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_OPEN, 0.8f, 1f)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.view.topInventory != GachaShopGui.gachaShopGui) return

        val player = event.player as Player
        player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_CLOSE, 0.8f, 1f)
    }
}

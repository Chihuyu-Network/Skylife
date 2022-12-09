package love.chihuyu.skylife.barter

import love.chihuyu.skylife.barter.BarterGui.gui
import love.chihuyu.skylife.barter.BarterGui.page
import love.chihuyu.skylife.barter.constants.Areas
import love.chihuyu.skylife.barter.constants.Panels
import love.chihuyu.skylife.data.ItemDataManager
import love.chihuyu.skylife.util.*
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.PlayerInventory

object BarterEvent : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.view.topInventory != gui[event.whoClicked]) return

        event.isCancelled = true

        val clickedItem = event.currentItem ?: return
        val clickedSlot = event.slot
        val player = event.whoClicked as Player
        val clickedInvType = event.clickedInventory?.type
        val playerInv = event.view.bottomInventory as PlayerInventory
        val barterInv = event.view.topInventory

        if (clickedInvType == playerInv.type) playerInv.moveTo(barterInv, clickedSlot)

        if (clickedInvType == barterInv.type) {
            if (clickedSlot in Areas.trading) barterInv.moveTo(playerInv, clickedSlot)

            if (clickedSlot in listOf(38, 47)) {
                val page = if (clickedSlot == 38) page[player]!!.dec() else page[player]!!.inc()
                val isValid = if (clickedSlot == 38) page >= 0 else barterInv.last() != Panels.fill

                val sound = if (isValid) Sound.UI_BUTTON_CLICK else MEOW
                if (isValid) BarterGui.page[player] = page

                player.playSound(player.location, sound, 0.8f, 1f)
            }

            if (clickedSlot in Areas.tradable) {
                if (!ItemDataManager.isTradable(clickedItem.type)) return

                val tradableItems = ItemDataManager.getTradableItems(clickedItem.type)
                val amount = when (event.click) {
                    ClickType.LEFT -> 1
                    ClickType.SHIFT_LEFT -> 64
                    ClickType.RIGHT -> 32
                    ClickType.SHIFT_RIGHT -> 16
                    else -> 0
                }
                val removedCount = barterInv.removeAsPossible(amount) { slot, item ->
                    slot in Areas.trading && tradableItems.contains(item.type)
                }

                playerInv.addOrDropItem(ItemUtil.create(clickedItem.type, count = removedCount))
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1f)
            }
        }

        repeat(page[player]!! + 1) {
            BarterGui.update(player)
            player.updateInventory()

            if (barterInv.getItem(3) != Panels.fill) return@repeat

            page[player]!!.dec()
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.view.topInventory != gui[event.player]) return

        val playerInv = event.view.bottomInventory as PlayerInventory
        val barterInv = event.view.topInventory
        val player = event.player as Player

        Areas.trading.forEach { barterInv.moveOrDropTo(playerInv, it) }
        BarterGui.update(player)
        player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_CLOSE, 0.8f, 1f)
    }

    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        if (event.view.topInventory != gui[event.player]) return

        val player = event.player as Player

        player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_OPEN, 0.8f, 1f)
    }
}

package love.chihuyu.skylife.gachastorage

import love.chihuyu.skylife.data.GachaData
import love.chihuyu.skylife.database.GachaStorages
import love.chihuyu.skylife.database.Users
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
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object GachaStorageEvent : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.view.topInventory != GachaStorageGui.gui[event.whoClicked]) return

        event.isCancelled = true

        val clickedInv = event.clickedInventory ?: return
        val playerInv = event.view.bottomInventory as PlayerInventory
        if (clickedInv.type == playerInv.type) return
        val clickedItem = clickedInv.getItem(event.slot) ?: return
        val id = clickedItem.getCustomModelDataOrNull() ?: return
        val gacha = GachaData.pairCustomData[id - 10000] ?: return
        val player = event.whoClicked as Player
        val amount = when (event.click) {
            ClickType.SHIFT_LEFT -> 64
            ClickType.SHIFT_RIGHT -> 16
            ClickType.LEFT -> 1
            ClickType.RIGHT -> 32
            else -> 0
        }
        val removedCount = clickedInv.removeAsPossible(amount, clickedItem.type)
        transaction {
            val row = (Users innerJoin GachaStorages)
                .select { Users.uuid eq player.uniqueId }
                .single()

            val column = when (gacha.customModelData) {
                5010 -> GachaStorages.kinrokansha
                5030 -> GachaStorages.shokuryo
                5040 -> GachaStorages.kenzai
                else -> return@transaction
            }

            GachaStorages.update({ GachaStorages.id eq row[Users.gachaStorageId] }) {
                it[column] = row[column] - removedCount
            }
        }
        val sound = if (removedCount == 0) MEOW else Sound.ENTITY_EXPERIENCE_ORB_PICKUP
        playerInv.addOrDropItem(gacha.getItemStack(removedCount))
        player.playSound(player.location, sound, 0.8f, 1f)
        GachaStorageGui.update(player)
        player.updateInventory()
    }

    @EventHandler
    fun onOpen(event: InventoryOpenEvent) {
        if (event.view.topInventory != GachaStorageGui.gui[event.player]) return
        val player = event.player as Player

        GachaStorageGui.update(player)
        player.updateInventory()
        player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_OPEN, 0.8f, 1f)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.view.topInventory != GachaStorageGui.gui[event.player]) return

        val player = event.player as Player
        player.playSound(player.location, Sound.BLOCK_ENDER_CHEST_CLOSE, 0.8f, 1f)
    }
}

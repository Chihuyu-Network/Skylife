package love.chihuyu.skylife.gachastorage

import love.chihuyu.skylife.data.GachaData
import love.chihuyu.skylife.database.GachaStorages
import love.chihuyu.skylife.database.Users
import love.chihuyu.skylife.util.ItemUtil
import org.bukkit.Bukkit.createInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object GachaStorageGui {
    val gui = hashMapOf<Player, Inventory>()

    private val enabled = with(GachaStorages) { listOf(kenzai, shokuryo, kinrokansha) }

    fun create(player: Player) = createInventory(null, 9, "GachaStorage").also { gui[player] = it }

    fun open(player: Player) {
        if (!gui.containsKey(player)) create(player)

        player.openInventory(gui[player]!!)
    }

    fun update(player: Player) {
        val gui = gui[player]!!
        val row = transaction {
            (Users innerJoin GachaStorages).slice(enabled)
                .select { Users.uuid eq player.uniqueId }
                .single()
        }

        enabled.forEach {
            GachaData.data[it.name]?.let { gacha ->
                val item = ItemUtil.create(
                    gacha.material,
                    gacha.name,
                    row[it],
                    false,
                    gacha.lore,
                    gacha.customModelData + 10000
                )
                gui.addItem(item)
            }
        }
    }
}

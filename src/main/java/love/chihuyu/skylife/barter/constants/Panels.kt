package love.chihuyu.skylife.barter.constants

import love.chihuyu.skylife.util.ItemUtil
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material

object Panels {
    val fill = ItemUtil.create(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ")
    val separate = ItemUtil.create(Material.WHITE_STAINED_GLASS_PANE, " ")
    val nextPage = ItemUtil.create(Material.LIME_STAINED_GLASS_PANE, "${ChatColor.GREEN}Next Page")
    val prevPage = ItemUtil.create(Material.RED_STAINED_GLASS_PANE, "${ChatColor.RED}Previous Page")
}

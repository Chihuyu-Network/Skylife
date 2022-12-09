package love.chihuyu.skylife.database

import org.jetbrains.exposed.sql.Table

object Stats : Table() {
    val id = integer("id").autoIncrement()

    val coin = integer("coin").default(0)
    val foodConsumed = integer("food_consumed").default(0)
    val blockPlaced = integer("block_placed").default(0)
    val toolBroken = integer("tool_broken").default(0)

    override val primaryKey = PrimaryKey(id)
}

package love.chihuyu.skylife.database

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val uuid = uuid("uuid")

    val statsId = integer("stats_id") references Stats.id
    val gachaStorageId = integer("gacha_storage_id") references GachaStorages.id

    override val primaryKey = PrimaryKey(uuid)
}

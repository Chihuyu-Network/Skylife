package love.chihuyu.skylife.util

import org.jetbrains.exposed.sql.*

fun Table.increment(
    column: Column<Int>,
    value: Int = 1,
    where: (SqlExpressionBuilder.() -> Op<Boolean>)
) {
    this.update(where) {
        with(SqlExpressionBuilder) {
            it.update(column, column + value)
        }
    }
}

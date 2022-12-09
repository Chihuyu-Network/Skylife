package love.chihuyu.skylife.util

import kotlin.random.Random

class Randomizer<T>(vararg chances: Pair<T, Int>) {
    private val chanceMap = chances.toMap()
    private val chanceSum = chanceMap.values.sum()

    fun choose(): T {
        var tmp = 0
        val rnd = Random.nextInt(chanceSum)
        return chanceMap.entries.first { tmp += it.value; rnd < tmp }.key
    }
}

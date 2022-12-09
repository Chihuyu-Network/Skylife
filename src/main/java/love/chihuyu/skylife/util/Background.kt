package love.chihuyu.skylife.util

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// HACK:
// REVIEW:
// OPTIMIZE:
fun background(block: suspend () -> Unit) {
    runBlocking {
        launch {
            block()
        }
    }
}

package org.athenian

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    withoutClose()
    withClose()
}

fun withoutClose() {
    runBlocking {
        val channel = Channel<Int>()
        launch {
            repeat(5) {
                channel.send(it * it)
                delay(System.currentTimeMillis() % 1000)
            }
        }

        repeat(5) { println(channel.receive()) }
    }
    println("withoutClose complete")
}

fun withClose() {
    runBlocking {
        val channel = Channel<Int>()
        launch {
            repeat(5) {
                channel.send(it * it)
                delay(System.currentTimeMillis() % 1000)
            }
            channel.close()
        }

        for (y in channel) println(y)
    }
    println("withClose complete")

}

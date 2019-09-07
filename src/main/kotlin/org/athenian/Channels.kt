package org.athenian

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
fun main() {
    withoutClose()
    withClose()
}

@ExperimentalTime
fun withoutClose() {
    runBlocking {
        val channel = Channel<Int>()
        launch {
            repeat(5) {
                channel.send(it * it)
                delay(Random.nextLong(1_000).milliseconds)
            }
        }

        repeat(5) { println(channel.receive()) }
    }
    println("withoutClose complete")
}

@ExperimentalTime
fun withClose() {
    runBlocking {
        val channel = Channel<Int>()
        launch {
            repeat(5) {
                channel.send(it * it)
                delay(Random.nextLong(1_000).milliseconds)
            }
            channel.close()
        }

        for (y in channel) println(y)
    }
    println("withClose complete")
}

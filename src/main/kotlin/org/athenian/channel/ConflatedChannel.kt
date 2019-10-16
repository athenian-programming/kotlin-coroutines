package org.athenian.channel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.milliseconds

fun main() {
    runBlocking {
        val channel = Channel<Int>(Channel.CONFLATED)

        launch {
            repeat(10) {
                println("Writing $it")
                channel.send(it)
                delay(200.milliseconds)
            }
            channel.close()
        }

        while (!channel.isClosedForReceive) {
            println("Reading ${channel.receive()}")
            delay(1_000.milliseconds)
        }
    }
}
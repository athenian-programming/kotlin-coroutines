package org.athenian.channel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ExperimentalTime
fun main() {
    val iterations = 10
    runBlocking {
        val channel = Channel<Int>(Channel.RENDEZVOUS)

        launch {
            repeat(iterations) {
                println("Writing $it")
                channel.send(it)
                delay(2_000.milliseconds)
            }
            channel.close()
        }

        // Fast reader
        repeat(iterations / 2) {
            println("Fast Reading ${channel.receive()}")
        }

        // Slow reader
        repeat(iterations / 2) {
            println("Slow Reading ${channel.receive()}")
            delay(4_000.milliseconds)
        }
    }
}
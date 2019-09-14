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
        val channel = Channel<Int>(Channel.UNLIMITED)

        launch {
            repeat(iterations) {
                println("Writing $it")
                channel.send(it)
            }
            channel.close()
        }

        repeat(iterations) {
            println("Reading ${channel.receive()}")
            delay(1_000.milliseconds)
        }
    }
}
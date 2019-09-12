package org.athenian

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
fun main() {
    val iterations = 10

    runBlocking {
        val channel = Channel<Deferred<String>>()

        launch {
            repeat(iterations) {
                val d = channel.receive()
                val s = d.await()
                println("Received value: $s")
            }
        }

        launch {
            repeat(iterations) {
                val d =
                    async {
                        delay(100.milliseconds)
                        "This is a value: $it"
                    }
                channel.send(d)
            }
        }

    }
}
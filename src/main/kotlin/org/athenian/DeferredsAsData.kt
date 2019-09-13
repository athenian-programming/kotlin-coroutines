package org.athenian

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
fun main() {
    val iterations = 4

    runBlocking {
        val channel = Channel<Deferred<String>>()

        launch {
            repeat(iterations) {
                val d = channel.receive()
                println("Waiting for value $it")
                val s = d.await()
                println("Received value: $s")
            }
        }

        launch {
            repeat(iterations) { i ->
                val cs = if (i % 2 == 0) CoroutineStart.DEFAULT else CoroutineStart.LAZY
                val d =
                    async(start = cs) {
                        println("Calculating value $i")
                        delay(10.milliseconds)
                        "Calculated value $i"
                    }
                println("\nSending value $i")
                channel.send(d)
                delay(100.milliseconds)
            }
        }
    }
}
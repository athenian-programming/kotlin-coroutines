package org.athenian

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@ExperimentalTime
@ExperimentalCoroutinesApi
fun main() {

    class Receiver(val id: Int, val channel: BroadcastChannel<Int>) {
        suspend fun listen() {
            for (v in channel.openSubscription()) {
                println("Receiver $id read value: $v")

                // Introduce a delay to see a pause for all reads to take place
                if (id == 0)
                    delay(3.seconds)
            }
        }
    }

    val workerCount = 3
    val channelCapacity = 5
    val iterations = 10

    val channel = BroadcastChannel<Int>(channelCapacity)
    val receivers = List(workerCount) { Receiver(it, channel) }

    runBlocking {
        receivers
            .onEach {
                launch {
                    it.listen()
                }
            }

        repeat(iterations) {
            println("Sending value $it")
            channel.send(it)
            delay(10.milliseconds)
        }

        channel.close()
    }
}
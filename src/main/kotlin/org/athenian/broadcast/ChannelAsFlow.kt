package org.athenian.broadcast

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.seconds

fun main() {
  open class Receiver(val id: Int, val flow: Flow<Int>) {
    open suspend fun listen() {
      flow.onStart { println("Starting Receiver $id") }
        .onEach {
          println("Receiver $id read value: $it")
          // Introduce a delay to see a pause for all reads to take place
          delay(2.seconds)
        }
        .onCompletion { println("Completed Receiver $id") }
        .collect { println("Collected Receiver $id") }
    }
  }

  val workerCount = 3
  val channelCapacity = 5
  val iterations = 10
  val channel = BroadcastChannel<Int>(channelCapacity)

  runBlocking {
    // Start each of the receivers in a separate coroutine
    (1..workerCount).forEach {
      launch {
        delay(2.seconds)
        Receiver(it, channel.asFlow()).listen()
      }
    }

    // Send values to receivers
    repeat(iterations) {
      println("Sending value $it")
      channel.send(it)
      delay(1.seconds)
    }

    // Close channel inside coroutine scope
    channel.close()
  }
}
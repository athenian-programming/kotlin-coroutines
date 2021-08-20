package org.athenian.sharedflow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.Duration.Companion.seconds

fun main() {
  open class FlowListener(val id: Int, val flow: Flow<Int>) {
    open suspend fun listen() {
      flow.onStart { println("Starting Receiver $id") }
        .takeWhile { it != -1 }
        .onEach {
          println("Receiver $id read value: $it")
          // Introduce a delay to see a pause for all reads to take place
          delay(seconds(2))
        }
        .onCompletion { println("Completed Receiver $id") }
        .collect { println("Collected Receiver $id") }
    }
  }

  val workerCount = 3
  val channelCapacity = 5
  val iterations = 10
  val sharedFlow = MutableSharedFlow<Int>(channelCapacity)

  runBlocking {
    // Start each of the receivers in a separate coroutine
    repeat(workerCount) { i ->
      launch {
        delay(seconds(2))
        FlowListener(i, sharedFlow).listen()
      }
    }

    // Send values to receivers
    repeat(iterations) { i ->
      println("Sending value $i")
      sharedFlow.emit(i)
      delay(seconds(1))
    }

    // Stop Receiver
    sharedFlow.emit(-1)
  }
}
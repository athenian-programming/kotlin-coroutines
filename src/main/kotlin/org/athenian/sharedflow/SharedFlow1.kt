package org.athenian.sharedflow

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
  val workerCount = 3
  val channelCapacity = 5
  val iterations = 10
  val sharedFlow = MutableSharedFlow<Int>(channelCapacity)

  runBlocking {
    // Start each of the receivers in a separate coroutine
    repeat(workerCount) { i ->
      launch {
        delay(seconds(2))
        sharedFlow
          .onStart { println("Starting Listening $i") }
          .takeWhile { it != -1 }
          .onEach {
            // Introduce a delay to see a pause for all reads to take place
            delay(seconds(2))
          }
          .onCompletion { println("Completed Listening $i") }
          .collect { println("Receiver $i read value: $it") }
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
package org.athenian.flow

import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration.Companion.seconds

// See https://proandroiddev.com/an-early-look-at-kotlin-coroutines-flow-62e46baa6eb0
// Demonstrates a hot stream -- values are produced regardless of a consumer being present

fun main() {
  runBlocking {
    val hot =
      produce(capacity = 5) {
        repeat(100) {
          send(it)
          log("sent $it")
        }
      }

    delay(seconds(5))

    repeat(5) {
      log("Recieved ${hot.receive()}")
      delay(seconds(1))
    }

    log("Cancel hot")
    hot.cancel()

    val cold =
      flow {
        repeat(100) {
          emit(it)
          log("emitted $it")
        }
      }

    delay(seconds(5))

    cold.take(5)
      .onEach { delay(seconds(1)) }
      .collect { log("Collected $it") }
  }
}
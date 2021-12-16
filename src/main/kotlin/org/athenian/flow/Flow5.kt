package org.athenian.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

fun main() {

  val cold: Flow<Int> =
    flow {
      repeat(5) {
        emit(it)
        log("emitted $it")
        delay(1.seconds)
      }
    }


  val unbuffered =
    measureTime {
      runBlocking {
        cold
          .onEach { delay(1.seconds) }
          .collect { log("Collected $it") }
      }
    }

  val buffered =
    measureTime {
      runBlocking {
        cold
          .buffer()
          .onEach { delay(1.seconds) }
          .collect { log("Collected $it") }
      }
    }

  println("Unbuffered time: $unbuffered Buffered time: $buffered")
}
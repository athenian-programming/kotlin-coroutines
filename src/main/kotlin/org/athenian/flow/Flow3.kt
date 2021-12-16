package org.athenian.flow

import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

// See https://medium.com/@elizarov/kotlin-flows-and-coroutines-256260fb3bdb

fun main() {
  fun withSequences() {
    val seqVals =
      sequence {
        repeat(500) {
          Thread.sleep(10)
          yield(it)
        }
      }
    val (v, dur) =
      measureTimedValue {
        var counter = 0
        for (i in seqVals) {
          Thread.sleep(10)
          counter++
        }
        counter
      }
    log("Total time for $v values withSequences(): $dur")
  }

  fun withFlow(useBuffer: Boolean) {
    val flowVals =
      flow {
        repeat(500) {
          delay(10.milliseconds)
          emit(it)
        }
      }
    val (v, dur) =
      measureTimedValue {
        runBlocking {
          var counter = 0
          if (useBuffer)
            flowVals
              .buffer()
              .onEach { delay(10.milliseconds) }
              .collect { counter++ }
          else
            flowVals
              .onEach { delay(10.milliseconds) }
              .collect { counter++ }
          counter
        }
      }
    log("Total time for $v vals withFlow(${useBuffer}): $dur")
  }

  withSequences()
  withFlow(false)
  withFlow(true)
}
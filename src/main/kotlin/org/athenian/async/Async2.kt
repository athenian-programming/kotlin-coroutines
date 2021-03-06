package org.athenian.async

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.measureTimedValue
import kotlin.time.seconds

const val reps = 10

fun main() {
  runBlocking {
    val (val1, dur1) =
      measureTimedValue {
        (1..reps)
          .map {
            delay(1.seconds)
            it
          }
          .sumBy { it }
      }
    log("val1 = $val1 in $dur1")

    val (val2, dur2) =
      measureTimedValue {
        (1..reps)
          .map {
            async {
              delay(1.seconds)
              it
            }
          }
          .sumBy { it.await() }
      }
    log("val2 = $val2 in $dur2")
  }
}

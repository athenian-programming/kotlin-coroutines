package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue
import kotlin.time.seconds

fun main() {
  val count = 100_000
  val delay = 1.seconds

  measureTimedValue {
    runBlocking {
      List(count) {
        launch {
          delay(delay)
          print(".")
        }
      }.forEach { it.join() }
    }
  }.also { log("\nFinished $count iterations in ${it.duration}") }
}

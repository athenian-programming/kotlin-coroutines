package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTime
import kotlin.time.seconds

fun main() {
  val count = 100_000
  val delay = 1.seconds

  measureTime {
    runBlocking {
      List(count) {
        launch {
          delay(delay)
          print(".")
        }
      }.forEach { it.join() }
    }
  }.apply { log("\nFinished $count iterations in $this") }
}

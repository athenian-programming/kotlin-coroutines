package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

fun main() {
  val count = 100_000
  val delay = seconds(1)

  measureTime {
    runBlocking {
      val jobs =
        List(count) {
          launch {
            delay(delay)
            print(".")
          }
        }
      jobs.forEach { it.join() }
    }
  }.apply { println("\nFinished in $this") }
}
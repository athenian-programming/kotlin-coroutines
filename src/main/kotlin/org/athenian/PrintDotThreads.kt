package org.athenian

import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.measureTime

fun main() {
  val count = 100_000
  val delay = Duration.seconds(1)

  measureTime {
    val jobs =
      List(count) {
        thread {
          Thread.sleep(delay.inWholeMilliseconds)
          print(".")
        }
      }
    jobs.forEach { it.join() }
  }.apply { log("\nFinished in $this") }
}



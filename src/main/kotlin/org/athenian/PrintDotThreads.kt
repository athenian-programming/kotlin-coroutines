package org.athenian

import kotlin.concurrent.thread
import kotlin.time.measureTime
import kotlin.time.seconds

fun main() {
  val count = 100_000
  val delay = 1.seconds

  measureTime {
    val jobs =
      List(count) {
        thread {
          Thread.sleep(delay.toLongMilliseconds())
          print(".")
        }
      }
    jobs.forEach { it.join() }
  }.apply { log("\nFinished in $this") }
}



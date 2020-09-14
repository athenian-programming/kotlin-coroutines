package org.athenian

import kotlin.concurrent.thread
import kotlin.time.measureTimedValue
import kotlin.time.seconds

fun main() {
  val count = 100_000
  val delay = 1.seconds

  measureTimedValue {
    List(count) {
      thread {
        Thread.sleep(delay.toLongMilliseconds())
        print(".")
      }
    }.forEach { it.join() }
  }.also { log("\nFinished in ${it.duration}") }
}

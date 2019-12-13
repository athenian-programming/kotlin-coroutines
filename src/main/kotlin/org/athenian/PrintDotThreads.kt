package org.athenian

import kotlin.concurrent.thread
import kotlin.time.measureTimedValue

fun main() {
  val count = 100_000
  val delayMillis = 1_000L

  val (_, dur) =
    measureTimedValue {
      val jobs =
        List(count) {
          thread {
            Thread.sleep(delayMillis)
            print(".")
          }
        }
      jobs.forEach { it.join() }
    }
  log("\nFinished in ${dur.toLongMilliseconds()}ms")
}

package org.athenian

import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

fun main() {
  val count = 100_000
  val delay = seconds(1)

  measureTime {
    val jobs =
      List(count) {
        thread {
          Thread.sleep(delay.inWholeMilliseconds)
          print(".")
        }
      }
    jobs.forEach { it.join() }
  }.apply { println("\nFinished in $this") }
}
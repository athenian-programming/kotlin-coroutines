package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration

fun main() {
  runBlocking {
    val job =
      launch {
        delay(Duration.milliseconds(1_000))
        println("${Thread.currentThread()} has run.")
      }

    println("Waiting for launch to complete")
    job.join()
  }
  println("Exited runBlocking")
}
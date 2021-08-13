package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration

fun main() {
  runBlocking {
    val jobs = List(10) {
      launch {
        delay(Duration.milliseconds(1000))
        println("${Thread.currentThread()} has run.")
      }
    }

    println("Waiting for launch list to complete")
    jobs.forEach { it.join() }
  }
  println("Exited runBlocking")
}
package org.athenian.deferred

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  val iterations = 4

  runBlocking {
    suspend fun execute(id: Int, deferred: Deferred<String>) {
      println("Waiting for value $id")
      val s = deferred.await()
      delay(milliseconds(10))
      println("Received value: $s")
    }

    repeat(iterations) { i ->
      println()
      val cs = if (i % 2 == 0) CoroutineStart.DEFAULT else CoroutineStart.LAZY
      val d =
        async(start = cs) {
          println("Calculating value $i")
          delay(milliseconds(10))
          "Async value $i"
        }
      delay(milliseconds(10))
      println("Sending value $i")
      execute(i, d)
      delay(milliseconds(100))
    }
  }
}
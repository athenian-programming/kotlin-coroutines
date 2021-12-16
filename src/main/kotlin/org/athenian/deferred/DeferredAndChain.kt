package org.athenian.deferred

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  val iterations = 4

  runBlocking {
    val defs =
      List(iterations) { it }
        .map { i ->
          val cs = if (i < iterations / 2) CoroutineStart.DEFAULT else CoroutineStart.LAZY
          Pair(i,
            async(start = cs) {
              println("Calculating value $i")
              delay(10.milliseconds)
              "Async value $i"
            })
        }
        .onEach { delay(100.milliseconds) }
        .onEach { (i, deferred) ->
          println("Waiting for value $i")
          println("Received value: ${deferred.await()}")
        }
    //.map { (_, deferred) -> deferred }

    //defs.awaitAll()
    //defs.forEach { println("Completed value ${it.getCompleted()}") }
  }
}
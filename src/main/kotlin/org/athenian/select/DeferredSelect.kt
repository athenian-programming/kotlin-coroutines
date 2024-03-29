package org.athenian.select

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.selects.selectUnbiased
import kotlinx.coroutines.withContext
import org.athenian.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun main() {
  class DeferredWrapper(val id: Int, val deferred: Deferred<Int>, var joined: Boolean = false)

  class Worker(val count: Int) {
    suspend fun selectDeferred(biased: Boolean) {
      val orderJoined = mutableListOf<Int>()

      coroutineScope {
        val wrappers =
          List(count) { i ->
            DeferredWrapper(i,
              async {
                delay(1.seconds)
                Random.nextInt()
              })
          }

        repeat(wrappers.size) {
          val selected: DeferredWrapper =
            if (biased)
              select {
                wrappers
                  .filter { !it.joined }
                  .onEach { taskInfo -> taskInfo.deferred.onAwait { result -> taskInfo } }
              }
            else
              selectUnbiased {
                wrappers
                  .filter { !it.joined }
                  .onEach { taskInfo -> taskInfo.deferred.onAwait { result -> taskInfo } }
              }
          orderJoined.add(selected.id)
          selected.joined = true
        }
      }

      println("\nBiased: $biased")
      println(orderJoined)
    }
  }

  runBlocking {
    val worker = Worker(100)

    withContext(Dispatchers.Default) { worker.selectDeferred(true) }
    withContext(Dispatchers.Default) { worker.selectDeferred(false) }
  }
}
package org.athenian.select

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.selects.selectUnbiased
import kotlinx.coroutines.withContext
import org.athenian.delay
import kotlin.time.Duration.Companion.seconds

fun main() {
  class JobWrapper(val id: Int, val job: Job, var joined: Boolean = false)

  class Worker(val count: Int) {
    suspend fun selectJobs(biased: Boolean) {
      val orderJoined = mutableListOf<Int>()

      coroutineScope {
        val wrappers = List(count) { i -> JobWrapper(i, launch { delay(1.seconds) }) }

        repeat(wrappers.size) {
          val selected: JobWrapper =
            if (biased)
              select {
                wrappers.filter { !it.joined }.onEach { it.job.onJoin { it } }
              }
            else
              selectUnbiased {
                wrappers.filter { !it.joined }.onEach { it.job.onJoin { it } }
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

    withContext(Dispatchers.Default) { worker.selectJobs(true) }
    withContext(Dispatchers.Default) { worker.selectJobs(false) }
  }
}
package org.athenian.globalscope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.athenian.log
import kotlin.time.measureTime

// See https://medium.com/@elizarov/the-reason-to-avoid-globalscope-835337445abc

fun main() {
  fun nosuspendWork(i: Int, desc: String) {
    Thread.sleep(1_000)
    log("Work $i for $desc done")
  }

  suspend fun suspendWork(i: Int, desc: String) {
    withContext(Dispatchers.Default) {
      nosuspendWork(i, desc)
    }
  }

  fun nonConcurrent() {
    log("Starting nonConcurrent()")
    measureTime {
      runBlocking {
        repeat(2) {
          launch {
            nosuspendWork(it, "nonConcurrent()")
          }
        }
      }
    }.also { log("nonConcurrent() done in $it") }
  }

  fun concurrent() {
    log("Starting concurrent()")
    measureTime {
      runBlocking {
        repeat(2) {
          launch(Dispatchers.Default) {
            nosuspendWork(it, "concurrent()")
          }
        }
      }
    }.also { log("concurrent() done in $it") }
  }

  fun differentScope() {
    log("Starting differentScope()")
    measureTime {
      runBlocking {
        repeat(2) {
          GlobalScope.launch {
            nosuspendWork(it, "differentScope()")
          }
        }
      }
    }.also { log("differentScope() done in $it") }
  }

  fun differentScopeWithJoin() {
    log("Starting differentScopeWithJoin()")
    measureTime {
      runBlocking {
        val jobs = mutableListOf<Job>()
        repeat(2) {
          jobs += GlobalScope.launch {
            nosuspendWork(it, "differentScopeWithJoin()")
          }
        }
        jobs.forEach { it.join() }
      }
    }.also { log("differentScopeWithJoin() done in $it") }
  }

  nonConcurrent()
  concurrent()
  differentScope()
  differentScopeWithJoin()
}
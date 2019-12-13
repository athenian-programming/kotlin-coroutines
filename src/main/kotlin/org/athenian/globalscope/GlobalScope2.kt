package org.athenian.globalscope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.athenian.log
import kotlin.time.measureTimedValue

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
    val (_, dur) =
      measureTimedValue {
        runBlocking {
          repeat(2) {
            launch {
              nosuspendWork(it, "nonConcurrent()")
            }
          }
        }
      }
    log("nonConcurrent() done in ${dur.toLongMilliseconds()}ms")
  }

  fun concurrent() {
    log("Starting concurrent()")
    val (_, dur) =
      measureTimedValue {
        runBlocking {
          repeat(2) {
            launch(Dispatchers.Default) {
              nosuspendWork(it, "concurrent()")
            }
          }
        }
      }
    log("concurrent() done in ${dur.toLongMilliseconds()}ms")
  }

  fun differentScope() {
    log("Starting differentScope()")
    val (_, dur) =
      measureTimedValue {
        runBlocking {
          repeat(2) {
            GlobalScope.launch {
              nosuspendWork(it, "differentScope()")
            }
          }
        }
      }
    log("differentScope() done in ${dur.toLongMilliseconds()}ms")
  }

  fun differentScopeWithJoin() {
    log("Starting differentScopeWithJoin()")
    val (_, dur) =
      measureTimedValue {
        runBlocking {
          val jobs = mutableListOf<Job>()
          repeat(2) {
            jobs += GlobalScope.launch {
              nosuspendWork(it, "differentScopeWithJoin()")
            }
          }
          jobs.forEach { it.join() }
        }
      }
    log("differentScopeWithJoin() done in ${dur.toLongMilliseconds()}ms")
  }

  nonConcurrent()
  concurrent()
  differentScope()
  differentScopeWithJoin()
}
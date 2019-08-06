package org.athenian

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// See https://medium.com/@elizarov/the-reason-to-avoid-globalscope-835337445abc

fun main() {
    nonConcurrent()
    concurrent()
    differentScope()
    differentScopeWithJoin()
}

fun nosuspendWork(i: Int, desc: String) {
    Thread.sleep(1000)
    log("Work $i for $desc done")
}

suspend fun suspendWork(i: Int, desc: String) {
    withContext(Dispatchers.Default) {
        nosuspendWork(i, desc)
    }
}

fun nonConcurrent() {
    log("Starting nonConcurrent()")
    val time = measureTimeMillis {
        runBlocking {
            repeat(2) {
                launch() {
                    nosuspendWork(it, "nonConcurrent()")
                }
            }
        }
    }
    log("nonConcurrent() done in $time ms")
}

fun concurrent() {
    log("Starting concurrent()")
    val time = measureTimeMillis {
        runBlocking {
            repeat(2) {
                launch(Dispatchers.Default) {
                    nosuspendWork(it, "concurrent()")
                }
            }
        }
    }
    log("concurrent() done in $time ms")
}

fun differentScope() {
    log("Starting differentScope()")
    val time = measureTimeMillis {
        runBlocking {
            repeat(2) {
                GlobalScope.launch() {
                    nosuspendWork(it, "differentScope()")
                }
            }
        }
    }
    log("differentScope() done in $time ms")
}

fun differentScopeWithJoin() {
    log("Starting differentScopeWithJoin()")
    val time = measureTimeMillis {
        runBlocking {
            val jobs = mutableListOf<Job>()
            repeat(2) {
                jobs += GlobalScope.launch() {
                    nosuspendWork(it, "differentScopeWithJoin()")
                }
            }
            jobs.forEach { it.join() }
        }
    }
    log("differentScopeWithJoin() done in $time ms")
}

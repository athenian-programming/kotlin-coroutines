package org.athenian

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


fun main() {
    val count = 100_000
    threadedAtomicInt(count)
    executorAtomicInt(count)
    coroutineAtomicInt(count)
    variableContextCounter(count, false)
    variableContextCounter(count, true)
}

fun threadedAtomicInt(count: Int) {
    val atomic = AtomicInteger(0)
    var nonatomic = 0

    val time_ms =
        measureTimeMillis {
            List(count) {
                thread {
                    nonatomic++
                    atomic.incrementAndGet()
                }
            }.forEach { thread -> thread.join() }
        }

    log("Threaded atomic: ${atomic.get()} nonatomic: $nonatomic finished in ${time_ms}ms")
}

fun executorAtomicInt(count: Int) {
    val executor = Executors.newFixedThreadPool(10)
    val atomic = AtomicInteger(0)
    var nonatomic = 0

    val time_ms =
        measureTimeMillis {
            List(count) {
                executor.submit {
                    nonatomic++
                    atomic.incrementAndGet()
                }
            }.forEach { future -> future.get() }
        }
    executor.shutdown()

    log("Executor atomic: ${atomic.get()} nonatomic: $nonatomic finished in ${time_ms}ms")
}

fun coroutineAtomicInt(count: Int) {
    val atomic = AtomicInteger(0)
    var nonatomic = 0
    var mutexcnt = 0

    val time_ms =
        measureTimeMillis {
            runBlocking {
                val mutex = Mutex()
                repeat(count) {
                    // Use Dispatchers.Default to use multiple threads
                    launch(Dispatchers.Default) {
                        // log("Incrementing")
                        nonatomic++
                        atomic.incrementAndGet()
                        mutex.withLock { mutexcnt++ }
                    }
                }
            }
        }

    log("Coroutine atomic: ${atomic.get()} mutex: $mutexcnt nonatomic: $nonatomic finished in ${time_ms}ms")
}

fun variableContextCounter(count: Int, singleThreaded: Boolean) =
    runBlocking(Dispatchers.Default) {
        val singleThreadedContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        var nonatomic = 0
        val context = if (singleThreaded) singleThreadedContext else Dispatchers.Default

        val time_ms =
            measureTimeMillis {
                withContext(context) {
                    repeat(count) {
                        launch {
                            nonatomic++
                        }
                    }
                }
            }
        singleThreadedContext.close()

        log("Variable context (single threaded = $singleThreaded) nonatomic: $nonatomic finished in ${time_ms}ms")
    }



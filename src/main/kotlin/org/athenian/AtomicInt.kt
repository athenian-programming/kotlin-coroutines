package org.athenian

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.CountDownLatch
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
            val latch = CountDownLatch(count)

            repeat(count) {
                thread {
                    nonatomic++
                    atomic.incrementAndGet()
                    latch.countDown()
                }
            }
            latch.await()
        }

    log("Threaded atomic: ${atomic.get()} nonatomic: $nonatomic finished in ${time_ms}ms")
}

fun executorAtomicInt(count: Int) {
    val executor = Executors.newFixedThreadPool(5)
    val atomic = AtomicInteger(0)
    var nonatomic = 0

    val time_ms =
        measureTimeMillis {
            val latch = CountDownLatch(count)

            repeat(count) {
                executor.submit {
                    nonatomic++
                    atomic.incrementAndGet()
                    latch.countDown()
                }
            }
            latch.await()
        }
    executor.shutdownNow()

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
    runBlocking {
        val singleThreadedContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        var counter = 0
        val context = if (singleThreaded) singleThreadedContext else Dispatchers.Default

        val time_ms =
            measureTimeMillis {
                withContext(context) {
                    repeat(count) {
                        launch {
                            counter++
                        }
                    }
                }
            }
        singleThreadedContext.close()

        log("Variable context (single threaded = $singleThreaded): $counter finished in ${time_ms}ms")
    }



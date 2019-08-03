package org.athenian

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


fun main() {
    val count = 100_000
    coroutineAtomicInt(count)
    threadedAtomicInt(count)
    executorAtomicInt(count)
}

fun coroutineAtomicInt(count: Int) {
    val time_ms =
        measureTimeMillis {
            val mutex = Mutex()
            val atomic = AtomicInteger(0)
            var nonatomic = 0
            var mutexcnt = 0
            runBlocking {
                repeat(count) {
                    // Use Dispatchers.Default to involve multiple threads
                    launch(Dispatchers.Default) {
                        // log("Incrementing")
                        atomic.addAndGet(1)
                        //mutex.withLock {
                        //mutexcnt++
                        //}
                        nonatomic++

                    }
                }
            }

            log("Coroutine atomic counter: ${atomic.get()} mutex counter: $mutexcnt nonatomic counter: $nonatomic")
        }
    log("Coroutine finished in ${time_ms}ms")
}

fun threadedAtomicInt(count: Int) {
    val time_ms =
        measureTimeMillis {
            val atomic = AtomicInteger(0)
            var nonatomic = 0
            val latch = CountDownLatch(count)

            repeat(count) {
                thread {
                    atomic.addAndGet(1)
                    nonatomic++
                    latch.countDown()
                }
            }
            latch.await()

            log("Threaded atomic counter: ${atomic.get()} nonatomic counter: $nonatomic")
        }

    log("Threaded finished in ${time_ms}ms")
}

fun executorAtomicInt(count: Int) {
    val time_ms =
        measureTimeMillis {
            val atomic = AtomicInteger(0)
            var nonatomic = 0
            val latch = CountDownLatch(count)
            val executor = Executors.newFixedThreadPool(5)

            repeat(count) {
                executor.submit {
                    atomic.addAndGet(1)
                    nonatomic++
                    latch.countDown()
                }
            }
            latch.await()
            executor.shutdown()

            log("Executor atomic counter: ${atomic.get()} nonatomic counter: $nonatomic")
        }

    log("Executor finished in ${time_ms}ms")
}
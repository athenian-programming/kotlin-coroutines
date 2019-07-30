package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


fun main() {
    val count = 100_000
    coroutineAtomicInt(count)
    threadedAtomicInt(count)
}

fun coroutineAtomicInt(count: Int) {
    val time_ms =
        measureTimeMillis {
            val counter = AtomicInteger(0)

            runBlocking {
                (1..count).forEach {
                    launch {
                        counter.addAndGet(1)
                    }
                }
            }

            log("Coroutine counter = ${counter.get()}")
        }
    log("Coroutine finished in ${time_ms}ms")
}

fun threadedAtomicInt(count: Int) {
    val time_ms =
        measureTimeMillis {
            val counter = AtomicInteger(0)
            val latch = CountDownLatch(count)

            (1..count).forEach {
                thread {
                    counter.addAndGet(1)
                    latch.countDown()
                }
            }
            latch.await()

            log("Threaded counter = ${counter.get()}")
        }

    log("Threaded finished in ${time_ms}ms")
}

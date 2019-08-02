package org.athenian

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

fun main() {
    val pool = Executors.newFixedThreadPool(20)
    val poolDispatcher = pool.asCoroutineDispatcher()

    for (count in listOf(8, 9, 16, 17)) {

        val sleepingTime1 = measureTimeMillis {
            runBlocking {
                repeat(count) {
                    launch(CoroutineName("Dispatchers.Default-item-$it")) {
                        sleepingCall(Dispatchers.Default)
                    }
                }
            }
        }
        log("Total time for $count calls of sleepingCalls with Dispatchers.Default: ${sleepingTime1 / 1000}secs")
        log()

        val sleepingTime2 = measureTimeMillis {
            runBlocking {
                repeat(count) {
                    launch(CoroutineName("poolDispatcher-item-$it")) {
                        sleepingCall(poolDispatcher)
                    }
                }
            }
        }
        log("Total time for $count calls of sleepingCalls with poolDispatcher: ${sleepingTime2 / 1000}secs")
        log()


        val delayingTime = measureTimeMillis {
            runBlocking {
                repeat(count) {
                    launch(CoroutineName("delaying-item-$it")) {
                        delayingCall()
                    }
                }
            }
        }
        log("Total time for $count calls of delayingCalls: ${delayingTime / 1000}secs")
        log()
    }

    pool.shutdown()
}

suspend fun sleepingCall(context: CoroutineContext) {
    withContext(context) {
        log("sleeping")
        Thread.sleep(3000)
    }
}

suspend fun delayingCall() {
    log("delaying")
    delay(3000)
}

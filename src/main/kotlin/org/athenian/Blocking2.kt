package org.athenian

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

fun main() {

    Executors.newFixedThreadPool(20).asCoroutineDispatcher()
        .use { poolDispatcher ->
            for (count in listOf(8, 9, 16, 17)) {

                val millis1 =
                    measureTimeMillis {
                        runBlocking {
                            repeat(count) {
                                launch(CoroutineName("Dispatchers.Default-item-$it")) {
                                    sleepingCall(Dispatchers.Default)
                                }
                            }
                        }
                    }
                log("Total time for $count calls of sleepingCalls with Dispatchers.Default: ${millis1 / 1_000}secs\n")

                val millis2 =
                    measureTimeMillis {
                        runBlocking {
                            repeat(count) {
                                launch(CoroutineName("poolDispatcher-item-$it")) {
                                    sleepingCall(poolDispatcher)
                                }
                            }
                        }
                    }
                log("Total time for $count calls of sleepingCalls with poolDispatcher: ${millis2 / 1_000}secs\n")

                val millis3 =
                    measureTimeMillis {
                        runBlocking {
                            repeat(count) {
                                launch(CoroutineName("delaying-item-$it")) {
                                    delayingCall()
                                }
                            }
                        }
                    }
                log("Total time for $count calls of delayingCalls: ${millis3 / 1_000}secs\n")
            }
        }
}

suspend fun sleepingCall(context: CoroutineContext) {
    withContext(context) {
        log("sleeping")
        Thread.sleep(3_000)
    }
}

suspend fun delayingCall() {
    log("delaying")
    delay(3_000)
}

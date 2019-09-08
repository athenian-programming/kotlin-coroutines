package org.athenian.blocking

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.athenian.delay
import org.athenian.log
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlin.time.seconds

@ExperimentalTime
fun main() {
    Executors.newFixedThreadPool(20).asCoroutineDispatcher()
        .use { poolDispatcher ->
            for (count in listOf(8, 9, 16, 17)) {
                val (_, dur1) =
                    measureTimedValue {
                        runBlocking {
                            repeat(count) {
                                launch(CoroutineName("Dispatchers.Default-item-$it")) {
                                    sleepingCall(Dispatchers.Default)
                                }
                            }
                        }
                    }
                log("Total time for $count calls of sleepingCalls with Dispatchers.Default: ${dur1.inSeconds.toInt()} secs\n")

                val (_, dur2) =
                    measureTimedValue {
                        runBlocking {
                            repeat(count) {
                                launch(CoroutineName("poolDispatcher-item-$it")) {
                                    sleepingCall(poolDispatcher)
                                }
                            }
                        }
                    }
                log("Total time for $count calls of sleepingCalls with poolDispatcher: ${dur2.inSeconds.toInt()} secs\n")

                val (_, dur3) =
                    measureTimedValue {
                        runBlocking {
                            repeat(count) {
                                launch(CoroutineName("delaying-item-$it")) {
                                    delayingCall()
                                }
                            }
                        }
                    }
                log("Total time for $count calls of delayingCalls: ${dur3.inSeconds.toInt()} secs\n")
            }
        }
}

suspend fun sleepingCall(context: CoroutineContext) {
    withContext(context) {
        log("sleeping")
        Thread.sleep(3_000)
    }
}

@ExperimentalTime
suspend fun delayingCall() {
    log("delaying")
    delay(3.seconds)
}

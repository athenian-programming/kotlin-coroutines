package org.athenian

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() {
    for (count in listOf(8, 9, 16, 17)) {
        val sleepingTime = measureTimeMillis {
            runBlocking {
                repeat(count) {
                    launch {
                        log("Launching sleepingCall #$it")
                        sleepingCall()
                    }
                }
            }
        }
        log("Total time for $count calls of sleepingCalls: ${sleepingTime / 1000}secs")
        log()

        val delayingTime = measureTimeMillis {
            runBlocking {
                repeat(count) {
                    launch {
                        log("Launching delayingCall #$it")
                        delayingCall()
                    }
                }
            }
        }
        log("Total time for $count calls of delayingCalls: ${delayingTime / 1000}secs")
        log()
    }
}

suspend fun sleepingCall() {
    withContext(Dispatchers.Default) {
        Thread.sleep(3000)
    }
}

suspend fun delayingCall() {
    delay(3000)
}

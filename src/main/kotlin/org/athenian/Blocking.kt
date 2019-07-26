package org.athenian

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() {
    for (count in listOf(8, 9, 16, 17)) {
        val sleepingTime = measureTimeMillis {
            runBlocking {
                repeat(count) {
                    launch {
                        println("Launching sleepingCall #$it")
                        sleepingCall()
                    }
                }
            }
        }
        println("Total time for $count calls of sleepingCalls: ${sleepingTime / 1000}secs")

        val delayingTime = measureTimeMillis {
            runBlocking {
                repeat(count) {
                    launch {
                        println("Launching delayingCall #$it")
                        delayingCall()
                    }
                }
            }
        }
        println("Total time for $count calls of delayingCalls: ${delayingTime / 1000}secs")
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

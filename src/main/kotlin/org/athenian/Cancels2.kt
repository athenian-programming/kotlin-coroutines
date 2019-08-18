package org.athenian

import kotlinx.coroutines.*

fun main() {
    runBlocking {
        val job =
            launch {
                log("Coroutine start")
                launch {
                    log("Child coroutine start")
                    try {
                        delay(Long.MAX_VALUE)
                    } catch (e: CancellationException) {
                        log("Coroutine cancelled - ${e.message}")
                        throw e
                    }
                }
            }
        delay(1_000)
        job.cancelAndJoin()
        log("Done")
    }
}

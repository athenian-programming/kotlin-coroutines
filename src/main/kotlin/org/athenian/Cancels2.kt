package org.athenian

import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
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
        delay(1.seconds)
        job.cancelAndJoin()
        log("Done")
    }
}

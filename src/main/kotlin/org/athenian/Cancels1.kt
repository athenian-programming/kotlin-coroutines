package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
fun main() {
    runBlocking {
        val outerLaunch =
            launch {
                repeat(5) {
                    launch {
                        try {
                            delay(500.milliseconds)
                            log("Hello from first inner launch #$it")
                        } finally {
                            log("Departing #$it")
                        }
                    }
                }
            }

        log("Hello from runBlocking after outer launch")
        delay(100.milliseconds)
        outerLaunch.cancel()
    }
    log("Done")
}


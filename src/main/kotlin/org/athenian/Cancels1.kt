package org.athenian

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val outerLaunch =
            launch {
                repeat(5) {
                    launch {
                        try {
                            delay(500)
                            log("Hello from first inner launch #$it")
                        } finally {
                            log("Departing #$it")
                        }
                    }
                }
            }

        log("Hello from runBlocking after outer launch")
        delay(100)
        outerLaunch.cancel()
    }
    log("Done")
}


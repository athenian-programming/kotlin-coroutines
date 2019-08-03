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
                        while (true) {
                            delay(300)
                            log("Hello from first inner launch #$it")
                        }
                    }
                }
            }

        log("Hello from runBlocking after outer launch")
        delay(800)
        outerLaunch.cancel()
    }

    log("Done")
}


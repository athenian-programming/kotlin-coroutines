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
                        while (true) {
                            delay(300.milliseconds)
                            log("Hello from first inner launch #$it")
                        }
                    }
                }
            }.apply {
                invokeOnCompletion { log("Completed launch job") }
            }

        log("Hello from runBlocking after outer launch")
        delay(800.milliseconds)
        outerLaunch.cancel()
    }

    log("Done")
}


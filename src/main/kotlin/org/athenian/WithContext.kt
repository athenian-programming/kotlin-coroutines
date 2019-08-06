package org.athenian

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// See https://codinginfinite.com/kotlin-coroutines-best-practices-example/

fun main() {
    usingWithContext()
    usingLaunch()
}

fun usingWithContext() =
    runBlocking {
        val millis =
            measureTimeMillis {
                val job =
                    launch {
                        // withContext() invocation blocks
                        withContext(Dispatchers.Default) {
                            log("First task")
                            delay(1000)
                        }

                        log("Second task")
                        delay(1000)
                    }
                job.join()
            }
        log("Finished usingWithContext() in ${millis}ms")
    }

fun usingLaunch() =
    runBlocking {
        val millis =
            measureTimeMillis {
                val job =
                    launch {
                        // launch() invocation does not block
                        launch(Dispatchers.Default) {
                            log("First task")
                            delay(1000)
                        }

                        log("Second task")
                        delay(1000)
                    }
                job.join()
            }
        log("Finished usingLaunch() in ${millis}ms")
    }

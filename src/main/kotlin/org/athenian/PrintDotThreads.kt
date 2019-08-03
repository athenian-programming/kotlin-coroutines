package org.athenian

import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

fun main() {
    val count = 100_000
    val delay_ms = 1_000L

    val millis =
        measureTimeMillis {
            val jobs =
                List(count) {
                    thread {
                        Thread.sleep(delay_ms)
                        print(".")
                    }
                }
            jobs.forEach { it.join() }
        }
    log()
    log("Finished in ${millis}ms")
}

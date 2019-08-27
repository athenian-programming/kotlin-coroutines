package org.athenian

import kotlin.concurrent.thread
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    val count = 100_000
    val delay_ms = 1_000L

    val (_, dur) =
        measureTimedValue {
            val jobs =
                List(count) {
                    thread {
                        Thread.sleep(delay_ms)
                        print(".")
                    }
                }
            jobs.forEach { it.join() }
        }
    log("\nFinished in ${dur.toLongMilliseconds()}ms")
}

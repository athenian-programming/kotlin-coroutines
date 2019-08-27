package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlin.time.seconds

@ExperimentalTime
fun main() {
    val (_, dur) =
        measureTimedValue {
            runBlocking {
                repeat(100_000) {
                    launch {
                        delay(1.seconds)
                        print(".")
                    }
                }
            }
        }

    log("\nFinished in ${dur.toLongMilliseconds()}ms")
}

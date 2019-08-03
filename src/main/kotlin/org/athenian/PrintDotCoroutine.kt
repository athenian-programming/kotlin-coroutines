package org.athenian

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {
    val count = 100_000
    val delay_ms = 1_000L

    val mills =
        measureTimeMillis {
            runBlocking {
                repeat(count) {
                    launch {
                        delay(delay_ms)
                        print(".")
                    }
                }
            }
        }
    log()
    log("Finished in ${mills}ms")
}

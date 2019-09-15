package org.athenian

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

// See: https://stackoverflow.com/questions/53535977/coroutines-runblocking-vs-coroutinescope

@ExperimentalTime
fun main() {
    fun withoutScope() =
        runBlocking {
            log("Coroutine scope begin")

            launch {
                delay(200.milliseconds)
                log("Task from runBlocking")
            }

            launch {
                delay(500.milliseconds)
                log("Task from nested launch")
            }

            delay(100.milliseconds)
            log("Task from coroutine scope")

            log("Coroutine scope end")
        }

    fun withScope() =
        runBlocking {
            log("Coroutine scope begin")

            launch {
                delay(200.milliseconds)
                log("Task from runBlocking")
            }

            coroutineScope {
                launch {
                    delay(500.milliseconds)
                    log("Task from nested launch")
                }

                delay(100.milliseconds)
                log("Task from coroutine scope")
            }

            log("Coroutine scope end")
        }

    println("\nwithoutScope()")
    withoutScope()

    println("\nwithScope()")
    withScope()
}
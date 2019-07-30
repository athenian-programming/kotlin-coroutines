package org.athenian

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    withoutScope()
    log()
    withScope()
}

fun withoutScope() =
    runBlocking {
        launch {
            delay(200L)
            log("Task from runBlocking")
        }

        launch {
            delay(500L)
            log("Task from nested launch")
        }

        delay(100L)
        log("Task from coroutine scope")

        log("Coroutine scope is over")
    }

fun withScope() =
    runBlocking {
        launch {
            delay(200L)
            log("Task from runBlocking")
        }

        coroutineScope {
            launch {
                delay(500L)
                log("Task from nested launch")
            }

            delay(100L)
            log("Task from coroutine scope")
        }

        log("Coroutine scope is over")
    }
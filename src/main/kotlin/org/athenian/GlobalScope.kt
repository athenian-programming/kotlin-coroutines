package org.athenian

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {
    log("With GlobalScope total time: ${measureTimeMillis { withGlobalScope() } / 1000}")
    log("Without GlobalScope total time: ${measureTimeMillis { withoutGlobalScope() } / 1000}")
}

fun withGlobalScope() {
    GlobalScope.launch {
        delay(1000L)
        log("World!")
    }

    log("Hello, ")

    runBlocking {
        delay(2000L)
    }
}

fun withoutGlobalScope() {
    runBlocking {
        launch {
            delay(1000L)
            log("World!")
        }

        log("Hello, ")
    }
}
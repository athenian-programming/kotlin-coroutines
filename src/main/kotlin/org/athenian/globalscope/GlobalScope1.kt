package org.athenian.globalscope

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlin.time.seconds

@ExperimentalTime
fun main() {
    fun withGlobalScope() {
        GlobalScope.launch {
            delay(1.seconds)
            log("World!")
        }

        log("Hello, ")

        runBlocking {
            delay(2.seconds)
        }
    }

    fun withoutGlobalScope() {
        runBlocking {
            launch {
                delay(2.seconds)
                log("there")
            }

            log("Hi ")
        }
    }

    val (_, dur1) = measureTimedValue { withGlobalScope() }
    val (_, dur2) = measureTimedValue { withoutGlobalScope() }

    log("With GlobalScope total time: ${dur1.inSeconds.toInt()} secs")
    log("Without GlobalScope total time: ${dur2.inSeconds.toInt()} secs")
}


package org.athenian

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

// See https://medium.com/@elizarov/kotlin-flows-and-coroutines-256260fb3bdb

@ExperimentalCoroutinesApi
fun main() {
    withSequences()
    withFlow(false)
    withFlow(true)
}

fun withSequences() {
    val seqVals =
        sequence {
            repeat(500) {
                Thread.sleep(10)
                yield(it)
            }
        }
    var counter = 0
    val millis =
        measureTimeMillis {
            for (i in seqVals) {
                Thread.sleep(10)
                counter++
            }
        }
    log("Total time for $counter vals withSequences(): ${millis}ms")
}

@ExperimentalCoroutinesApi
fun withFlow(useBuffer: Boolean) {
    val flowVals =
        flow {
            repeat(500) {
                delay(10)
                emit(it)
            }
        }
    var counter = 0
    val millis =
        measureTimeMillis {
            runBlocking {
                if (useBuffer)
                    flowVals
                        .buffer()
                        .onEach { delay(10) }
                        .collect { counter++ }
                else
                    flowVals
                        .onEach { delay(10) }
                        .collect { counter++ }

            }
        }
    log("Total time for $counter vals withFlow(${useBuffer}): ${millis}ms")
}
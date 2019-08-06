package org.athenian

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.delayEach
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

// See https://medium.com/@elizarov/kotlin-flows-and-coroutines-256260fb3bdb

@ExperimentalCoroutinesApi
fun main() {
    withSequences()
    flowNoBuffer()
    flowWithBuffer()
}

fun withSequences() {
    val seqVals =
        sequence {
            repeat(5) {
                Thread.sleep(500)
                yield(it)
            }
        }

    val millis =
        measureTimeMillis {
            for (i in seqVals) {
                Thread.sleep(500)
                println(i)
            }
        }
    log("Total time for withSequences(): ${millis}ms")
}

val flowVals =
    flow {
        repeat(5) {
            delay(500)
            emit(it)
        }
    }

@ExperimentalCoroutinesApi
fun flowNoBuffer() {
    val millis = measureTimeMillis {
        runBlocking {
            flowVals
                .delayEach(500)
                .collect { println(it) }
        }
    }
    log("Total time for flowNoBuffer(): ${millis}ms")
}

@ExperimentalCoroutinesApi
fun flowWithBuffer() {
    val millis = measureTimeMillis {
        runBlocking {
            flowVals
                .buffer()
                .delayEach(500)
                .collect { println(it) }
        }
    }
    log("Total time for flowWithBuffer(): ${millis}ms")
}
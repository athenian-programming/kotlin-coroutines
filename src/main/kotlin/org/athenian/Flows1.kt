package org.athenian

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

// See https://proandroiddev.com/an-early-look-at-kotlin-coroutines-flow-62e46baa6eb0
// Demonstrates a hot stream -- values are produced regardless of a consumer being present

@ExperimentalCoroutinesApi
@ExperimentalTime
fun main() {
    runBlocking {
        val hot =
            produce(capacity = 5) {
                repeat(100) {
                    send(it)
                    log("sent $it")
                }
            }

        delay(5.seconds)

        repeat(5) {
            log("Recieved ${hot.receive()}")
            delay(1.seconds)
        }

        log("Cancel hot")
        hot.cancel()

        val cold =
            flow {
                repeat(100) {
                    emit(it)
                    log("emitted $it")
                }
            }

        delay(5.seconds)

        cold.take(5)
            .onEach { delay(1.seconds) }
            .collect { log("Collected $it") }
    }
}
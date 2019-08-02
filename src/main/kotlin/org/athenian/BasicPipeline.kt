package org.athenian

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlin.random.Random

@ExperimentalCoroutinesApi
fun CoroutineScope.produceNumbers() =
    produce<Int> {
        var x = 1
        while (true) {
            log("Sending $x")
            send(x++)
        }
    }

@ExperimentalCoroutinesApi
fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> =
    produce {
        for (x in numbers) {
            log("Sending squared ${x * x}")
            send(x * x)
        }
    }

@ExperimentalCoroutinesApi
fun main() {
    runBlocking {
        val numbers = produceNumbers()
        val squares = square(numbers)

        repeat(5) {
            log("Received ${squares.receive()}")
            delay(Random.nextLong(2000))
        }

        coroutineContext.cancelChildren()

        log("Done!")
    }
}
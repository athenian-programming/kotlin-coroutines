package org.athenian

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

@ExperimentalCoroutinesApi
fun CoroutineScope.produceNumbers() =
    produce<Int> {
        var x = 1
        while (true) {
            println("Sending $x")
            send(x++)
        }
    }

@ExperimentalCoroutinesApi
fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> =
    produce {
        for (x in numbers) {
            println("Sending squared ${x * x}")
            send(x * x)
        }
    }

@ExperimentalCoroutinesApi
fun main() {
    runBlocking {
        val numbers = produceNumbers()
        val squares = square(numbers)

        repeat(5) {
            println("Received ${squares.receive()}")
            delay(System.currentTimeMillis() % 2000)
        }

        coroutineContext.cancelChildren()

        println("Done!")
    }
}
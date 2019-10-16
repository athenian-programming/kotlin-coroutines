package org.athenian

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.time.milliseconds

fun main() {
    fun CoroutineScope.produceNumbers() =
        produce {
            var x = 1
            while (true) {
                log("Sending $x")
                send(x++)
            }
        }

    fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> =
        produce {
            for (x in numbers) {
                log("Sending squared ${x * x}")
                send(x * x)
            }
        }

    runBlocking {
        val numbers = produceNumbers()
        val squares = square(numbers)

        repeat(5) {
            log("Received ${squares.receive()}")
            delay(Random.nextLong(2_000).milliseconds)
        }

        coroutineContext.cancelChildren()
    }
    log("Done")
}

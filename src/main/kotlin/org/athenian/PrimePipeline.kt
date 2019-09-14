package org.athenian

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main() {
    fun CoroutineScope.numbersFrom(start: Int) =
        produce {
            log("Creating numbersFrom")
            var x = start
            while (true)
                send(x++)
        }

    fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) =
        produce {
            log("Creating filter for $prime")
            for (x in numbers)
                if (x % prime != 0)
                    send(x)
                else
                    log("$x divisible by $prime")
        }

    runBlocking {
        var cur = numbersFrom(2)
        repeat(100) {
            val prime = cur.receive()
            log("#$it: $prime")
            cur = filter(cur, prime)
        }
        coroutineContext.cancelChildren()
    }
}
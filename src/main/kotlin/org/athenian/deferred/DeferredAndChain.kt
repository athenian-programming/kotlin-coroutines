package org.athenian.deferred

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
fun main() {
    val iterations = 4

    runBlocking {
        println("First approach")
        val defs =
            List(iterations) { it }
                .map { i ->
                    val cs = if (i % 2 == 0) CoroutineStart.DEFAULT else CoroutineStart.LAZY
                    Pair(i,
                        async(start = cs) {
                            //delay(10.milliseconds)
                            println("Calculating value $i")
                            "Async value $i"
                        })
                }
                .onEach { delay(100.milliseconds) }
                .forEach { (i, deferred) ->
                    println("Waiting for value $i")
                    println("Received value: ${deferred.await()}")
                }
    }
}
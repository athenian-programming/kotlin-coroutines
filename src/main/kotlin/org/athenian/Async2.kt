package org.athenian

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

val reps = 10

fun main() {
    runBlocking {

        var tot = 0

        val millis1 = measureTimeMillis {
            tot =
                (1..reps)
                    .map {
                        delay(1000)
                        it
                    }
                    .sumBy { it }
        }
        println("tot = $tot in ${millis1}ms")


        val millis2 = measureTimeMillis {
            tot =
                (1..reps)
                    .map {
                        async {
                            delay(1000)
                            it
                        }
                    }
                    .sumBy { it.await() }
        }
        println("tot = $tot in ${millis2}ms")

    }
}
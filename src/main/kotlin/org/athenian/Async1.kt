package org.athenian

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {

    runBlocking {

        val millis1 =
            measureTimeMillis {
                val a = calc()
                val b = calc()

                println("Vals = ${listOf(a, b)}")
            }
        println("Took ${millis1}ms")

        val millis2 =
            measureTimeMillis {
                val a = async { calc() }
                val b = async { calc() }

                println("Vals = ${listOf(a, b)}")
                println("Vals = ${listOf(a.await(), b.await())}")
            }
        println("Took ${millis2}ms")

        val millis3 =
            measureTimeMillis {
                val a = async(start = CoroutineStart.LAZY) { calc() }
                val b = async(start = CoroutineStart.LAZY) { calc() }

                println("Vals = ${listOf(a.await(), b.await())}")
            }
        println("Took ${millis3}ms")
    }
}

suspend fun calc(): String {
    delay(3000)
    return "A String Value"
}
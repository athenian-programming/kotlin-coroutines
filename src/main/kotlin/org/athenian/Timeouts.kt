package org.athenian

import kotlinx.coroutines.*

fun main() {
    runBlocking {
        timeout1()
        timeout2()
        timeout3()
    }
}

suspend fun timeout1() {
    try {
        withTimeout(1300) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500)
            }
        }
    } catch (e: TimeoutCancellationException) {
        println("Caught ${e.javaClass.simpleName}")
    }
}

suspend fun timeout2() {
    val result =
        withTimeoutOrNull(1300) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500)
            }
            "Done"
        }
    println("Result #1 is $result")
}

suspend fun timeout3() {
    val result =
        withTimeoutOrNull(1300) {
            repeat(2) { i ->
                println("I'm sleeping $i ...")
                delay(500)
            }
            "Done"
        }
    println("Result #2 is $result")
}
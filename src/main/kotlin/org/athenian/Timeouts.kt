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
            repeat(1_000) { i ->
                log("I'm sleeping $i ...")
                delay(500)
            }
        }
    } catch (e: TimeoutCancellationException) {
        log("Caught ${e.javaClass.simpleName}")
    }
}

suspend fun timeout2() {
    val result =
        withTimeoutOrNull(1300) {
            repeat(1_000) { i ->
                log("I'm sleeping $i ...")
                delay(500)
            }
            "Done"
        }
    log("Result #1 is $result")
}

suspend fun timeout3() {
    val result =
        withTimeoutOrNull(1_300) {
            repeat(2) { i ->
                log("I'm sleeping $i ...")
                delay(500)
            }
            "Done"
        }
    log("Result #2 is $result")
}
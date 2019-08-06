package org.athenian

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// See https://medium.com/@elizarov/explicit-concurrency-67a8e8fd9b25

fun main() {
    runBlocking {
        launchingCall()
        delayedCall1()
    }
}

// Declaring this as a CoroutineScope extension function enables an embedded call to launch()
fun CoroutineScope.launchingCall() {
    log("Starting launchingCall()")
    launch {
        delayedCall2()
    }
    log("Ending launchingCall()")
}

suspend fun delayedCall1() {
    log("Starting delayedCall1()")
    delay(1000)
    log("Ending delayedCall1()")
}

suspend fun delayedCall2() {
    log("Starting delayedCall2()")
    delay(1000)
    log("Ending delayedCall2()")
}
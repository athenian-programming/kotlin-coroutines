package org.athenian

import kotlinx.coroutines.*

fun main() {
    val i = 5

    log("Assigning deferred0")
    val deferred0 =
        (1..i)
            .map {
                log("Mapping $it in deferred0")
                it
            }
            .onEach { log("Completed deferred0") }
    log("Summing deferred0")
    log("Sum of deferred0: ${deferred0.sumBy { it }}")

    log()
    log("Assigning deferred1")
    val deferred1 =
        (1..i)
            .asSequence()
            .map {
                log("Mapping $it in deferred1")
                it
            }
            .onEach { log("Completed deferred1") }
    log("Summing deferred1")
    log("Sum of deferred1: ${deferred1.sumBy { it }}")

    log()
    log("Assigning deferred2")
    val deferred2 =
        (1..i)
            .map {
                log("Mapping $it in deferred2")
                GlobalScope.async {
                    log("Returning $it in deferred2")
                    it
                }
            }
            .onEach { log("Completed deferred2") }
    runBlocking {
        delay(100)
        log("Summing deferred2")
        log("Sum of deferred2: ${deferred2.sumBy { it.await() }}")
    }

    log()
    log("Assigning deferred3")
    val deferred3 =
        (1..i)
            .map {
                log("Mapping $it in deferred3")
                GlobalScope.async(start = CoroutineStart.LAZY) {
                    log("Returning $it in deferred3")
                    it
                }
            }
            .onEach { log("Completed deferred3") }
    runBlocking {
        delay(100)
        log("Summing deferred3")
        log("Sum of deferred3: ${deferred3.sumBy { it.await() }}")
    }
}
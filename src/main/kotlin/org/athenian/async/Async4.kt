package org.athenian.async

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

val cnt = 5

@ExperimentalTime
fun main() {
    eagerNoAsync()
    sequenceNoAsync()
    nonlazyAsync()
    lazyAsync()
}

fun eagerNoAsync() {
    log("Assigning deferred0")
    val deferred0 =
        (1..cnt)
            .map {
                log("Mapping $it in deferred0")
                it
            }
            .onEach { log("Completed deferred0 for $it") }

    log("Summing deferred0")
    log("Sum of deferred0: ${deferred0.sumBy { it }}")
}

fun sequenceNoAsync() {
    log("\nAssigning deferred1")

    val deferred1 =
        (1..1_000)
            .asSequence()
            .map {
                log("Mapping $it in deferred1")
                it
            }
            .onEach { log("Completed deferred1 for $it") }
            .take(cnt)

    log("Summing deferred1")
    log("Sum of deferred1: ${deferred1.sumBy { it }}")
}

@ExperimentalTime
fun nonlazyAsync() {
    log("\nAssigning deferred2")

    val deferred2 =
        (1..cnt)
            .map {
                log("Mapping $it in deferred2")
                GlobalScope.async {
                    log("Returning $it in deferred2")
                    it
                }
            }
            .onEach { log("Completed deferred2 for $it") }

    runBlocking {
        delay(100.milliseconds)
        log("Summing deferred2")
        log("Sum of deferred2: ${deferred2.sumBy { it.await() }}")
    }
}

@ExperimentalTime
fun lazyAsync() {
    log("\nAssigning deferred3")
    val deferred3 =
        (1..cnt)
            .map {
                log("Mapping $it in deferred3")
                GlobalScope.async(start = CoroutineStart.LAZY) {
                    log("Returning $it in deferred3")
                    it
                }
            }
            .onEach { log("Completed deferred3 for $it") }

    runBlocking {
        delay(100.milliseconds)
        log("Summing deferred3")
        log("Sum of deferred3: ${deferred3.sumBy { it.await() }}")
    }

}
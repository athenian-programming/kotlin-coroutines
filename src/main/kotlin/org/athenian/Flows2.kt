package org.athenian

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

// See https://proandroiddev.com/an-early-look-at-kotlin-coroutines-flow-62e46baa6eb0

@ExperimentalCoroutinesApi
@ExperimentalTime
fun main() {
    flowExample()
    asFlowExample()
    flowOfExample()
    asFlowExample()
}

@ExperimentalCoroutinesApi
@ExperimentalTime
fun flowExample() {
    val intVals =
        flow {
            var i = 0
            while (true) {
                log("Emitting flowExample $i")
                emit(i++)
            }
        }

    runBlocking {
        intVals
            .take(5)
            .map { it * it }
            .onEach { delay(100.milliseconds) }
            .collect { log("Collecting flowExample $it") }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalTime
fun asFlowExample() =
    runBlocking {
        log()
        (1..100)
            .asFlow()
            .onStart { log("Starting asFlowExample") }
            .take(5)
            .map { it * it }
            .onEach { log("First asFlowExample onEach()") }
            .onEach { delay(100.milliseconds) }
            .flowOn(Dispatchers.Default) //changes upstream context
            .onEach { log("Second asFlowExample onEach()") }
            .map { it * 2 }
            .collect { log("Collecting asFlowExample $it") }
    }

@ExperimentalCoroutinesApi
@ExperimentalTime
fun flowOfExample() =
    runBlocking {
        log()
        flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .onStart { log("Starting flowOfExample") }
            .take(5)
            .map { it * it }
            .onEach { log("First flowOfExample onEach()") }
            .onEach { delay(100.milliseconds) }
            .flowOn(Dispatchers.Default) //changes upstream context
            .onEach { log("Second flowOfExample onEach()") }
            .map { it * 2 }
            .collect { log("Collecting flowOfExample $it") }
    }
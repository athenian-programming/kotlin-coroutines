package org.athenian

import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlin.time.seconds

@ExperimentalTime
fun main() {
    runBlocking {
        val (_, dur) =
            measureTimedValue {
                launch {
                    func1()
                    func2()
                }
            }
        log("Finished serial launch in ${dur.toLongMilliseconds()}ms")
    }

    runBlocking {
        val (_, dur) =
            measureTimedValue {
                launch {
                    func1()
                }
                launch {
                    func2()
                }
            }
        log("Finished concurrent launch in ${dur.toLongMilliseconds()}ms")
    }
    log("Done")
}

@ExperimentalTime
suspend fun func1() {
    withContext(Dispatchers.Default + CoroutineName("func1")) {
        delay(2.seconds)
        log("I am in func1")
    }
}

@ExperimentalTime
suspend fun func2() {
    withContext(Dispatchers.Default + CoroutineName("func2")) {
        delay(1.seconds)
        log("I am in func2")
    }
}
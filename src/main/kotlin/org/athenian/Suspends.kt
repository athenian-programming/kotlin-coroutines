package org.athenian

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() {
    runBlocking {
        val millis = measureTimeMillis {
            launch {
                func1()
                func2()
            }
        }
        log("Launched in ${millis}ms")
    }

    runBlocking {
        val millis = measureTimeMillis {
            launch {
                func1()
            }
            launch {
                func2()
            }
        }
        log("Launched in ${millis}ms")
    }
    log("Done")
}

suspend fun func1() {
    withContext(Dispatchers.Default + CoroutineName("func1")) {
        delay(2000)
        log("I am in func1")
    }
}

suspend fun func2() {
    withContext(Dispatchers.Default + CoroutineName("func2")) {
        delay(1000)
        log("I am in func2")
    }
}
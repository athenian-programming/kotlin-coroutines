package org.athenian

import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

fun main() {
    runBlocking {

        launch { scopeCheck(this) }


        launch {
            delay(200)
            log("Task from runBlocking")
        }

        coroutineScope {
            launch {
                delay(500)
                log("Task from nested launch")
            }

            delay(100)
            log("Task from coroutine scope")
        }

        log("Coroutine scope is over")
    }
}

suspend fun scopeCheck(scope: CoroutineScope) {
    log("coroutineContext are equal: ${scope.coroutineContext === coroutineContext}")
}
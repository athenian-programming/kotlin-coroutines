package org.athenian

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        launch {
            delay(200)
            println("Task from runBlocking")
        }

        coroutineScope {
            launch {
                delay(500)
                println("Task from nested launch")
            }

            delay(100)
            println("Task from coroutine scope")
        }

        println("Coroutine scope is over")
    }
}
package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main() {
    suspend fun task1() {
        log { "Enter task1" }
        yield()
        log { "Exit task1" }
    }

    suspend fun task2() {
        log { "Enter task2" }
        yield()
        log { "Exit task2" }
    }

    runBlocking {
        launch { task1() }
        launch { task2() }

        //yield()

        log { "Finished launching tasks" }
    }
}


package org.athenian.select

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex

fun selectOnUnlock() {
    // Initialize mutexes as locked
    val mutext0 = Mutex(true)
    val mutext1 = Mutex(true)
    val mutext2 = Mutex(true)

    runBlocking() {
        launch {
            repeat(3) {
                val selected =
                    select<String> {
                        mutext0.onLock { "mutex0" }
                        mutext1.onLock { "mutex1" }
                        mutext2.onLock { "mutex2" }
                    }
                println("Selected: $selected")
            }
        }

        println("Unlocking: mutext2")
        mutext2.unlock()

        delay(100)
        println("Unlocking: mutext1")
        mutext1.unlock()

        delay(100)
        println("Unlocking: mutext0")
        mutext0.unlock()
    }
}

fun selectOnLock() {
    // Initialize mutexes as unlocked
    val mutext0 = Mutex(false)
    val mutext1 = Mutex(false)
    val mutext2 = Mutex(false)

    runBlocking() {
        launch {
            repeat(3) {
                val selected =
                    select<String> {
                        mutext0.onLock { "mutex0" }
                        mutext1.onLock { "mutex1" }
                        mutext2.onLock { "mutex2" }
                    }
                println("Selected: $selected")
            }
        }


        println("Locking: mutext2")
        mutext2.lock()

        delay(100)
        println("Locking: mutext1")
        mutext1.lock()

        delay(100)
        println("Locking: mutext0")
        mutext0.lock()
    }
}

fun main() {
    // I woould not expect this to work
    println("Calling onUnlock()")
    selectOnUnlock()

    //I would expect this to work
    println("\nCalling onLock()")
    selectOnLock()
}
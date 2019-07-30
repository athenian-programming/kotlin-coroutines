package org.athenian

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    sleepFunction1()
    log()
    sleepFunction2()
    log()
    delayFunction()
}


fun sleepFunction1() {
    GlobalScope.launch() {
        log("Before first sleep")
        Thread.sleep(200)
        log("After first sleep")
    }

    log("Before second sleep")
    Thread.sleep(300)
    log("After second sleep")
}

fun sleepFunction2() {
    runBlocking {
        launch() {
            log("Before first sleep")
            Thread.sleep(200)
            log("After first sleep")
        }

        log("Before second sleep")
        Thread.sleep(300)
        log("After second sleep")
    }
}

fun delayFunction() {
    runBlocking {
        launch() {
            log("Before first delay")
            delay(200)
            log("After first delay")
        }

        log("Before second delay")
        delay(300)
        log("After second delay")
    }
}
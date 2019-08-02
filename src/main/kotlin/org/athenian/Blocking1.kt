package org.athenian

import kotlinx.coroutines.*

fun main() {
    sleepFunction1()
    sleepFunction2()
    sleepFunction3()
    delayFunction()
}


fun sleepFunction1() {
    log("sleepFunction1")
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
    log()
    log("sleepFunction2")
    runBlocking {
        launch {
            log("Before first sleep")
            Thread.sleep(200)
            log("After first sleep")
        }

        log("Before second sleep")
        Thread.sleep(300)
        log("After second sleep")
    }
}

fun sleepFunction3() {
    log()
    log("sleepFunction3")
    runBlocking {
        launch(Dispatchers.Default) {
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
    log()
    log("delayFunction")
    runBlocking {
        launch {
            log("Before first delay")
            delay(200)
            log("After first delay")
        }

        log("Before second delay")
        delay(300)
        log("After second delay")
    }
}
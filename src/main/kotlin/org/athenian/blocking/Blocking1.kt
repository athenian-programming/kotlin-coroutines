package org.athenian.blocking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
fun main() {
    sleepFunction1()
    sleepFunction2()
    sleepFunction3()
    delayFunction()
}


fun sleepFunction1() {
    log("sleepFunction1")
    GlobalScope.launch {
        log("Before first sleep")
        Thread.sleep(200)
        log("After first sleep")
    }

    log("Before second sleep")
    Thread.sleep(300)
    log("After second sleep")
}

fun sleepFunction2() {
    log("\nsleepFunction2")
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
    log("\nsleepFunction3")
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

@ExperimentalTime
fun delayFunction() {
    log("\ndelayFunction")
    runBlocking {
        launch {
            log("Before first delay")
            delay(200.milliseconds)
            log("After first delay")
        }

        log("Before second delay")
        delay(300.milliseconds)
        log("After second delay")
    }
}
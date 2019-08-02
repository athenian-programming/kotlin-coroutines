package org.athenian

import kotlinx.coroutines.*

fun main2() =
    runBlocking {
        val job = GlobalScope.launch(handler) {
            log("Throwing exception from launch")
            throw IndexOutOfBoundsException() // Will be printed to the console by Thread.defaultUncaughtExceptionHandler
        }
        job.join()
        log("Joined failed job")

        val deferred: Deferred<Int> = GlobalScope.async {
            delay(100)
            log("Throwing exception from async")
            throw ArithmeticException() // Nothing is printed, relying on user to call await
        }

        try {
            deferred.await()
            log("Unreached")
        } catch (e: ArithmeticException) {
            log("Caught an ArithmeticException")
        }
    }

fun main() {
    launchException()
    asyncException()
    log("Done")
}

val handler =
    CoroutineExceptionHandler { context, exception ->
        log("Handler caught $exception")
    }


fun launchException() {
    val j = GlobalScope.launch(handler) {
        log("Throwing exception")
        delay(100)
        throw IndexOutOfBoundsException("Problem encountered")
    }

    runBlocking {
        j.join()
        log("Finished launchException()")
    }
}

fun asyncException() {
    log()
    val deferred: Deferred<Int> =
        GlobalScope.async() {
            log("Throwing exception")
            throw IndexOutOfBoundsException("Problem encountered")
        }

    runBlocking(handler) {
        try {
            deferred.await()
        } catch (e: Exception) {
            log("Catch caught ${e.javaClass.simpleName}")
            //e.printStackTrace()
        }
    }
    log("Finished asyncException()")
}


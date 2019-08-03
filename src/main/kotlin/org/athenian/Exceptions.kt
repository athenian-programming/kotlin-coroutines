package org.athenian

import kotlinx.coroutines.*

// The key to this working properly is that the launch and async calls use a different CoroutineScope

@InternalCoroutinesApi
fun main() {
    launchException()
    asyncException()
    log("Done")
}

val handler =
    CoroutineExceptionHandler { context, exception ->
        log("Handler caught $exception")
    }

@InternalCoroutinesApi
fun launchException() {
    val job =
        GlobalScope.launch(handler) {
            log("Throwing exception")
            delay(100)
            throw IndexOutOfBoundsException()
        }

    runBlocking {
        job.join()
        log("Caught cancellation exception: ${job.getCancellationException().cause!!.javaClass.simpleName}")
    }
    log("Finished launchException()")
}

fun asyncException() {
    log()
    val deferred: Deferred<Int> =
        GlobalScope.async() {
            log("Throwing exception")
            throw IndexOutOfBoundsException()
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


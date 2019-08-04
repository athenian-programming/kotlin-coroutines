package org.athenian

import kotlinx.coroutines.*

// The key to this working properly is that the launch and async calls use a different CoroutineScope
// See https://proandroiddev.com/coroutines-snags-6bf6fb53a3d1 for other details
// Also see https://proandroiddev.com/kotlin-coroutines-patterns-anti-patterns-f9d12984c68e

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
    // Create a custom CoroutineScope
    val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    log()
    val deferred: Deferred<Int> =
        appScope.async() {
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


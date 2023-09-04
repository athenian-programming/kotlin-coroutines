package org.athenian

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

// The key to this working properly is that the launch and async calls use a different CoroutineScope
// See https://proandroiddev.com/coroutines-snags-6bf6fb53a3d1 for other details
// Also see https://proandroiddev.com/kotlin-coroutines-patterns-anti-patterns-f9d12984c68e

fun main() {
  fun launchException() {
    runBlocking {
      val job =
        launch {
          try {
            withContext(Dispatchers.Default + CoroutineName("launchException")) {
              log("Throwing exception")
              delay(100.milliseconds)
              throw IndexOutOfBoundsException()
            }
          } catch (e: Exception) {
            log("Caught exception ${e.javaClass.simpleName}")
          }
        }
      job.join()
      log("Caught cancellation exception: ${
        job.getCancellationException().cause?.javaClass?.simpleName
          ?: "None"
      }")

    }
    log("Finished launchException()")
  }

  val handler =
    CoroutineExceptionHandler { context, e ->
      log("Handler caught $e")
    }

  fun launchWithHandlerException() {
    log()
    val job =
      GlobalScope.launch(handler) {
        log("Throwing exception")
        delay(100.milliseconds)
        throw IndexOutOfBoundsException()
      }

    runBlocking {
      job.join()
      log("Caught cancellation exception: ${
        job.getCancellationException().cause?.javaClass?.simpleName
          ?: "None"
      }")
    }
    log("Finished launchWithHandlerException()")
  }

  fun asyncException() {
    log()

    // Create a custom CoroutineScope
    val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val deferred: Deferred<Int> =
      appScope.async {
        log("Throwing exception in asyncException")
        throw IndexOutOfBoundsException()
      }

    runBlocking(handler) {
      try {
        deferred.await()
      } catch (e: Exception) {
        log("asyncException caught ${e.javaClass.simpleName}")
      }
    }
    log("Finished asyncException()")
  }

  launchException()
  launchWithHandlerException()
  asyncException()

  log("Done")
}


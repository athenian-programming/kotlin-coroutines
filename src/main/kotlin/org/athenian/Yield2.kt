package org.athenian

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
  val job = launch {
    val child = launch {
      try {
        println("Child is starting")
        delay(Long.MAX_VALUE.seconds)
      } finally {
        println("Child is cancelled")
      }
    }
    // Without this, the child will never get a chance to start.
    // Without the yield, we can use: launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()).
    // yield()
    println("Cancelling child")
    child.cancel()
    child.join()
    //yield()
    println("Parent is not cancelled")
  }
  job.join()
}

package org.athenian.cancel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration.Companion.seconds

fun main() {
  runBlocking {
    val job =
      launch {
        log("Coroutine start")
        launch {
          log("Child coroutine start")
          try {
            delay(Long.MAX_VALUE.seconds)
          } catch (e: CancellationException) {
            log("Coroutine cancelled - ${e.message}")
            throw e
          }
        }
      }
    delay(1.seconds)
    job.cancelAndJoin()
    log("Done")
  }
}
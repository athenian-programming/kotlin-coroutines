package org.athenian.cancel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.log
import kotlin.time.Duration

fun main() {
  runBlocking {
    val job =
      launch {
        log("Coroutine start")
        launch {
          log("Child coroutine start")
          try {
            delay(Long.MAX_VALUE)
          } catch (e: CancellationException) {
            log("Coroutine cancelled - ${e.message}")
            throw e
          }
        }
      }
    org.athenian.delay(Duration.seconds(1))
    job.cancelAndJoin()
    log("Done")
  }
}
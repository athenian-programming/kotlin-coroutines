package org.athenian

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.time.measureTime
import kotlin.time.seconds

// See https://codinginfinite.com/kotlin-coroutines-best-practices-example/

fun main() {
  fun usingWithContext() =
    runBlocking {
      measureTime {
        val job =
          launch {
            // withContext() invocation blocks
            withContext(Dispatchers.Default) {
              log("First task")
              delay(1.seconds)
            }

            log("Second task")
            delay(1.seconds)
          }
        job.join()
      }.also { log("Finished usingWithContext() in $it") }
    }

  fun usingLaunch() =
    runBlocking {
      measureTime {
        val job =
          launch {
            // launch() invocation does not block
            launch(Dispatchers.Default) {
              log("First task")
              delay(1.seconds)
            }

            log("Second task")
            delay(1.seconds)
          }
        job.join()
      }.also { log("Finished usingLaunch() in $it") }
    }

  usingWithContext()
  usingLaunch()
}
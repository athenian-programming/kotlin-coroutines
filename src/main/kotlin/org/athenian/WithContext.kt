package org.athenian

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.time.measureTimedValue
import kotlin.time.seconds

// See https://codinginfinite.com/kotlin-coroutines-best-practices-example/

fun main() {
  fun usingWithContext() =
    runBlocking {
      val (_, dur) =
        measureTimedValue {
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
        }
      log("Finished usingWithContext() in ${dur.toLongMilliseconds()}ms")
    }

  fun usingLaunch() =
    runBlocking {
      val (_, dur) =
        measureTimedValue {
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
        }
      log("Finished usingLaunch() in ${dur.toLongMilliseconds()}ms")
    }

  usingWithContext()
  usingLaunch()
}
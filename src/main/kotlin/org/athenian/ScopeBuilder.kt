package org.athenian

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration

// See: https://stackoverflow.com/questions/53535977/coroutines-runblocking-vs-coroutinescope

fun main() {
  fun withoutScope() =
    runBlocking {
      log("Coroutine scope begin")

      launch {
        delay(Duration.milliseconds(200))
        log("Task from runBlocking")
      }

      launch {
        delay(Duration.milliseconds(500))
        log("Task from nested launch")
      }

      delay(Duration.milliseconds(100))
      log("Task from coroutine scope")

      log("Coroutine scope end")
    }

  fun withScope() =
    runBlocking {
      log("Coroutine scope begin")

      launch {
        delay(Duration.milliseconds(200))
        log("Task from runBlocking")
      }

      coroutineScope {
        launch {
          delay(Duration.milliseconds(500))
          log("Task from nested launch")
        }

        delay(Duration.milliseconds(100))
        log("Task from coroutine scope")
      }

      log("Coroutine scope end")
    }

  println("\nwithoutScope()")
  withoutScope()

  println("\nwithScope()")
  withScope()
}
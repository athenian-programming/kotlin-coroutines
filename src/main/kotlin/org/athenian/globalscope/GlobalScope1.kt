package org.athenian.globalscope

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration
import kotlin.time.measureTime

fun main() {
  fun withGlobalScope() {
    GlobalScope.launch {
      delay(Duration.seconds(1))
      log("World!")
    }

    log("Hello, ")

    runBlocking {
      delay(Duration.seconds(2))
    }
  }

  fun withoutGlobalScope() {
    runBlocking {
      launch {
        delay(Duration.seconds(2))
        log("there")
      }

      log("Hi ")
    }
  }

  val dur1 = measureTime { withGlobalScope() }
  val dur2 = measureTime { withoutGlobalScope() }

  log("With GlobalScope total time: $dur1")
  log("Without GlobalScope total time: $dur2")
}


package org.athenian.globalscope

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.measureTime
import kotlin.time.seconds

fun main() {
  fun withGlobalScope() {
    GlobalScope.launch {
      delay(1.seconds)
      log("World!")
    }

    log("Hello, ")

    runBlocking {
      delay(2.seconds)
    }
  }

  fun withoutGlobalScope() {
    runBlocking {
      launch {
        delay(2.seconds)
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


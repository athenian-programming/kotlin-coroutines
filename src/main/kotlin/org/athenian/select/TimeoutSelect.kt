package org.athenian.select

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import org.athenian.delay
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  suspend fun execute() {
    coroutineScope {
      val job = launch { delay(100.milliseconds) }
      val selected =
        select {
          job.onJoin { "Joined job" }
          onTimeout(10.milliseconds.inWholeMilliseconds) { "Timed out" }
        }
      println(selected)
    }
  }

  runBlocking {
    execute()
  }
}
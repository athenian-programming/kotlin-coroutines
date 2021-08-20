package org.athenian.select

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.athenian.delay
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  suspend fun execute() {
    coroutineScope {
      val job = launch { delay(milliseconds(100)) }
      val selected =
        select<String> {
          job.onJoin { "Joined job" }
          onTimeout(milliseconds(10).inWholeMilliseconds) { "Timed out" }
        }
      println(selected)
    }
  }

  runBlocking {
    execute()
  }
}
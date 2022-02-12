package org.athenian.scan

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.runBlocking

// See https://kt.academy/article/cc-scan

object Scan1 {
  @JvmStatic
  fun main(args: Array<String>) {
    runBlocking {
      flowOf(1, 2, 3, 4)
        .onEach { delay(1000) }
        .scan(0) { acc, v -> acc + v }
        .collect { println(it) }
    }
  }
}
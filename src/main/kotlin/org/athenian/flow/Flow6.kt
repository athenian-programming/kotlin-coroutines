package org.athenian.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow

// From https://kt.academy/article/cc-flow-building

suspend fun main() {
  val function = suspend {
    // this is suspending lambda expression
    delay(1000)
    "UserName"
  }

  function.asFlow()
    .collect { println(it) }
}
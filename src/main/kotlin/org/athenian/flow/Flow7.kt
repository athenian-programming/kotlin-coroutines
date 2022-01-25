package org.athenian.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow

// From https://kt.academy/article/cc-flow-building

suspend fun getUserName(): String {
  delay(1000)
  return "UserName"
}

suspend fun main() {
  ::getUserName
    .asFlow()
    .collect { println(it) }
}


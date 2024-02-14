package org.athenian

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() {
  val msf = MutableStateFlow(0)

  runBlocking {
    launch {
      msf.collect { println("First: $it") }
    }

    for (i in 1..5) {
      msf.value = i
      delay(1.seconds)
    }
  }
}
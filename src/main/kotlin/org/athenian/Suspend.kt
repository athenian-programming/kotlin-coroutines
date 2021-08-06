package org.athenian

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.measureTime

fun main() {
  suspend fun func1() {
    withContext(Dispatchers.Default + CoroutineName("func1")) {
      delay(Duration.seconds(2))
      log("I am in func1")
    }
  }

  suspend fun func2() {
    withContext(Dispatchers.Default + CoroutineName("func2")) {
      delay(Duration.seconds(1))
      log("I am in func2")
    }
  }

  runBlocking {
    measureTime {
      launch {
        func1()
        func2()
      }
    }.also { log("Finished serial launch in $it") }
  }

  runBlocking {
    measureTime {
      launch {
        func1()
      }
      launch {
        func2()
      }
    }.also { log("Finished concurrent launch in $it") }
  }
  log("Done")
}


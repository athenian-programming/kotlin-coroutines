package org.athenian

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.time.measureTimedValue
import kotlin.time.seconds

fun main() {
  suspend fun func1() {
    withContext(Dispatchers.Default + CoroutineName("func1")) {
      delay(2.seconds)
      log("I am in func1")
    }
  }

  suspend fun func2() {
    withContext(Dispatchers.Default + CoroutineName("func2")) {
      delay(1.seconds)
      log("I am in func2")
    }
  }

  runBlocking {
    val (_, dur) =
      measureTimedValue {
        launch {
          func1()
          func2()
        }
      }
    log("Finished serial launch in ${dur.toLongMilliseconds()}ms")
  }

  runBlocking {
    val (_, dur) =
      measureTimedValue {
        launch {
          func1()
        }
        launch {
          func2()
        }
      }
    log("Finished concurrent launch in ${dur.toLongMilliseconds()}ms")
  }
  log("Done")
}


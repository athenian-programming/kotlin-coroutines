package org.athenian.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  suspend fun scopeCheck(scope: CoroutineScope) {
    log("coroutineContext are equal: ${scope.coroutineContext === coroutineContext}")
  }

  runBlocking {

    launch { scopeCheck(this) }

    launch {
      delay(200.milliseconds)
      log("Task from runBlocking")
    }

    coroutineScope {
      launch {
        delay(500.milliseconds)
        log("Task from nested launch")
      }

      delay(100.milliseconds)
      log("Task from coroutine scope")
    }

    log("Coroutine scope is over")
  }
}


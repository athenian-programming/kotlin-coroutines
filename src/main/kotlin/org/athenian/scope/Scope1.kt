package org.athenian.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

fun main() {
  suspend fun scopeCheck(scope: CoroutineScope) {
    log("coroutineContext are equal: ${scope.coroutineContext === coroutineContext}")
  }

  runBlocking {

    launch { scopeCheck(this) }

    launch {
      delay(Duration.milliseconds(200))
      log("Task from runBlocking")
    }

    coroutineScope {
      launch {
        delay(Duration.milliseconds(500))
        log("Task from nested launch")
      }

      delay(Duration.milliseconds(100))
      log("Task from coroutine scope")
    }

    log("Coroutine scope is over")
  }
}


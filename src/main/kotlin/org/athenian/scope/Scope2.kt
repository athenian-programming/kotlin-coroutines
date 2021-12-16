package org.athenian.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration.Companion.seconds

// See https://medium.com/@elizarov/explicit-concurrency-67a8e8fd9b25

fun main() {
  suspend fun delayedCall1() {
    log("Starting delayedCall1()")
    delay(1.seconds)
    log("Ending delayedCall1()")
  }

  suspend fun delayedCall2() {
    log("Starting delayedCall2()")
    delay(1.seconds)
    log("Ending delayedCall2()")
  }

  // Declaring this as a CoroutineScope extension function enables an embedded call to launch()
  fun CoroutineScope.launchingCall() {
    log("Starting launchingCall()")
    launch {
      delayedCall2()
    }
    log("Ending launchingCall()")
  }

  runBlocking {
    launchingCall()
    delayedCall1()
  }
}
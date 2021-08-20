package org.athenian.cancel

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  runBlocking {
    val outerLaunch =
      launch {
        repeat(5) {
          launch {
            while (true) {
              delay(milliseconds(300))
              log("Hello from first inner launch #$it")
            }
          }
        }
      }.apply {
        invokeOnCompletion { log("Completed launch job") }
      }

    log("Hello from runBlocking after outer launch")
    delay(milliseconds(800))
    outerLaunch.cancel()
  }

  log("Done")
}
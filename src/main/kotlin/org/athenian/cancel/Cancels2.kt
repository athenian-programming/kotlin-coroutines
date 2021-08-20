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
            try {
              delay(milliseconds(500))
              log("Hello from first inner launch #$it")
            } finally {
              log("Departing #$it")
            }
          }
        }
      }

    log("Hello from runBlocking after outer launch")
    delay(milliseconds(100))
    outerLaunch.cancel()
  }
  log("Done")
}
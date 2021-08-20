package org.athenian.async

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.athenian.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
  suspend fun calc(): String {
    delay(seconds(3))
    return "A string value"
  }

  runBlocking {
    val calcCall = async(Dispatchers.Default) { calc() }

    while (true) {
      val completed =
        withTimeoutOrNull(milliseconds(500).inWholeMilliseconds) {
          println("Waiting")
          println("Got back: ${calcCall.await()}")
        }

      if (completed == null)
        println("Timed out")
      else
        break
    }
  }
}
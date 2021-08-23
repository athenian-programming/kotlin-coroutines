package org.athenian.channel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  fun withoutClose() {
    runBlocking {
      val channel = Channel<Int>()
      launch {
        repeat(5) {
          channel.send(it * it)
          delay(milliseconds(Random.nextLong(1_000)))
        }
      }
      repeat(5) { println(channel.receive()) }
    }
    println("withoutClose complete")
  }

  fun withClose() {
    runBlocking {
      val channel = Channel<Int>()
      launch {
        repeat(5) {
          channel.send(it * it)
          delay(milliseconds(Random.nextLong(1_000)))
        }
        channel.close()
      }

      for (y in channel) println(y)
    }
    println("withClose complete")
  }

  withoutClose()
  withClose()
}
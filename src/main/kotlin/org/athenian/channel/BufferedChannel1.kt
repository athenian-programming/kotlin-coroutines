package org.athenian.channel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  val iterations = 10
  runBlocking {
    val channel = Channel<Int>(Channel.UNLIMITED)

    launch {
      repeat(iterations) {
        println("Writing $it")
        channel.send(it)
      }
      channel.close()
    }

    launch {
      repeat(iterations) {
        println("Reading ${channel.receive()}")
        delay(1_000.milliseconds)
      }
    }
  }
}
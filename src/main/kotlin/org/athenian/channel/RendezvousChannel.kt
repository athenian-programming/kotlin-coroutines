package org.athenian.channel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.Duration

fun main() {
  val iterations = 10
  runBlocking {
    val channel = Channel<Int>(Channel.RENDEZVOUS)

    launch {
      repeat(iterations) {
        println("Writing $it")
        channel.send(it)
        delay(Duration.milliseconds(2_000))
      }
      channel.close()
    }

    // Fast reader
    repeat(iterations / 2) {
      println("Fast Reading ${channel.receive()}")
    }

    // Slow reader
    repeat(iterations / 2) {
      println("Slow Reading ${channel.receive()}")
      delay(Duration.milliseconds(4_000))
    }
  }
}
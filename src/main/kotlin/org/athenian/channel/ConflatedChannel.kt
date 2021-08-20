package org.athenian.channel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  runBlocking {
    val channel = Channel<Int>(Channel.CONFLATED)

    launch {
      repeat(10) {
        println("Writing $it")
        channel.send(it)
        delay(milliseconds(200))
      }
      channel.close()
    }

    while (!channel.isClosedForReceive) {
      println("Reading ${channel.receive()}")
      delay(milliseconds(1_000))
    }
  }
}
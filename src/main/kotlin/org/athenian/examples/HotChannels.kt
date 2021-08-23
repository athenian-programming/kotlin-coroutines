package org.athenian.examples

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
  runBlocking {
    val ints: ReceiveChannel<Int> =
      produce(capacity = Channel.UNLIMITED) {
        for (i in 1..10) {
          println("Sending $i")
          send(i)
        }
      }

    println("Waiting to start")
    delay(3_000)

    repeat(5) {
      println("Read ${ints.receive()}")
    }
  }
}
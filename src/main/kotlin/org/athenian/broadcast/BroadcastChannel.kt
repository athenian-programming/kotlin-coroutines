package org.athenian.broadcast

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.athenian.delay
import kotlin.time.Duration

fun main() {
  open class Receiver(val id: Int, val channel: ReceiveChannel<Int>) {
    open suspend fun listen() {
      for (v in channel) {
        println("Receiver $id read value: $v")

        // Introduce a delay to see a pause for all reads to take place
        if (id == 0)
          delay(Duration.seconds(2))
      }
      println("Receiver $id completed")
    }
  }

  class ImpatientReceiver(id: Int, channel: ReceiveChannel<Int>) : Receiver(id, channel) {
    override suspend fun listen() {
      var active = true
      while (active) {
        active =
          select {
            channel.onReceiveCatching { v ->
              if (!v.isClosed) {
                println("Receiver $id read value: ${v.getOrNull()}")

                // Introduce a delay to see a pause for all reads to take place
                if (id == 0)
                  delay(Duration.seconds(2))
              }
              !v.isClosed
            }
            // Timeout after waiting 500ms for a read
            onTimeout(Duration.milliseconds(500).inWholeMilliseconds) {
              println("Receiver $id is impatient for values")
              true
            }
          }
      }
      println("Receiver $id completed")
    }
  }

  val workerCount = 3
  val channelCapacity = 5
  val iterations = 10
  val channel = BroadcastChannel<Int>(channelCapacity)
  val receivers =
    List(workerCount) {
      if (it == 1)
      // Create only a single impatient receiver
        ImpatientReceiver(it, channel.openSubscription())
      else
        Receiver(it, channel.openSubscription())
    }

  runBlocking {
    // Start each of the receivers in a separate coroutine
    receivers.forEach { launch { it.listen() } }

    // Send values to receivers
    repeat(iterations) {
      println("Sending value $it")
      channel.send(it)
      delay(Duration.milliseconds(10))
    }

    // Close channel inside coroutine scope
    channel.close()
  }
}
package org.athenian.sharedflow

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.athenian.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

interface Listener {
  suspend fun listen()
}

fun main() {
  class FlowListener(val id: Int, val channel: ReceiveChannel<Int>) : Listener {
    override suspend fun listen() {
      for (v in channel) {
        println("Receiver $id read value: $v")

        // Introduce a delay to see a pause for all reads to take place
        if (id == 0)
          delay(seconds(2))

        if (id == -1)
          break
      }
      println("Receiver $id completed")
    }
  }

  class ImpatientFlowListener(val id: Int, val channel: ReceiveChannel<Int>) : Listener {
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
                  delay(seconds(2))
              }
              !v.isClosed
            }
            // Timeout after waiting 500ms for a read
            onTimeout(milliseconds(500).inWholeMilliseconds) {
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
  val sharedFlow = MutableSharedFlow<Int>(channelCapacity)
  val receivers =
    List(workerCount) {
      // Create only a single impatient receiver
      if (it == 1)
        ImpatientFlowListener(it, sharedFlow.produceIn(GlobalScope))
      else
        FlowListener(it, sharedFlow.produceIn(GlobalScope))
    }

  runBlocking {
    // Start each of the receivers in a separate coroutine
    receivers.forEach { launch { it.listen() } }

    // Send values to receivers
    repeat(iterations) {
      println("Sending value $it")
      sharedFlow.emit(it)
      delay(milliseconds(10))
    }

    // Close channel inside coroutine scope
    //sharedFlow.close()
  }
}
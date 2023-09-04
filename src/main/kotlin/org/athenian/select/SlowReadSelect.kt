package org.athenian.select

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.athenian.delay
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun main() {
  class Results(val id: String, val total: Int)

  class Boss(
    val messageCount: Int,
    val slowWorker: SendChannel<Int>,
    val fastWorker: SendChannel<Int>,
    val results: List<ReceiveChannel<Results>>
  ) {
    suspend fun generateData() {
      repeat(messageCount) {
        val r = Random.nextInt()
        select {
          slowWorker.onSend(r) { }
          fastWorker.onSend(r) { }
        }
        delay(10.milliseconds)
      }
      slowWorker.close()
      fastWorker.close()
    }

    suspend fun aggregateData(): Map<String, Int> {
      val resultsMap = mutableMapOf<String, Int>()
      while (resultsMap.size < results.size)
        select {
          results
            .filter { !it.isClosedForReceive }
            .forEach {
              it.onReceiveCatching { value ->
                if (!value.isClosed)
                  resultsMap[value.getOrThrow().id] = value.getOrThrow().total
              }
            }
        }
      return resultsMap.toSortedMap()
    }
  }

  class Worker(
    val id: String,
    val delay: Duration,
    val channel: ReceiveChannel<Int>,
    val results: SendChannel<Results>
  ) {
    suspend fun process() {
      var counter = 0
      for (d in channel) {
        println("$id got value: $d")
        counter++
        delay(delay)
      }
      println("$id writing results")
      results.send(Results(id, counter))
    }
  }

  fun CoroutineScope.execute(messageCount: Int, slowDuration: Duration, fastDuration: Duration) {
    val slowData = Channel<Int>()
    val fastData = Channel<Int>()
    val results = List(2) { Channel<Results>() }
    val boss = Boss(messageCount, slowData, fastData, results)

    launch {
      Worker("Slow Worker", slowDuration, slowData, results[0]).process()
    }

    launch {
      Worker("Fast Worker", fastDuration, fastData, results[1]).process()
    }

    launch {
      boss.generateData()
    }

    launch {
      println(boss.aggregateData())
    }
  }

  runBlocking {
    execute(1_000, 100.milliseconds, 10.milliseconds)
  }
}
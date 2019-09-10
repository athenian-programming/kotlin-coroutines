package org.athenian.select

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.selects.selectUnbiased
import org.athenian.delay
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun main() {

    class Results(val id: String, val total: Int)

    class SelectReadBoss constructor(val messageCount: Int,
                                     val data: SendChannel<Duration>,
                                     val results: List<ReceiveChannel<Results>>) {

        suspend fun generateData() {
            repeat(messageCount) {
                data.send(Random.nextInt(10).milliseconds)
                delay(Random.nextInt(5).milliseconds)
            }
            data.close()
        }

        suspend fun aggregateData(biased: Boolean): Pair<MutableMap<String, Int>, List<Int>> {
            val resultsMap = mutableMapOf<String, Int>()
            val orderRead = mutableListOf<Int>()
            while (resultsMap.size < results.size) {
                val notClosedChannels = results.withIndex()
                    .filter { (i, channel) -> !channel.isClosedForReceive }
                if (biased)
                    select<Unit> {
                        notClosedChannels
                            .onEach { (i, channel) ->
                                channel.onReceiveOrClosed { value ->
                                    if (!value.isClosed) {
                                        resultsMap[value.value.id] = value.value.total
                                        orderRead.add(i)
                                    }
                                }
                            }
                    }
                else
                    selectUnbiased<Unit> {
                        notClosedChannels
                            .onEach { (i, channel) ->
                                channel.onReceiveOrClosed { value ->
                                    if (!value.isClosed) {
                                        resultsMap[value.value.id] = value.value.total
                                        orderRead.add(i)
                                    }
                                }
                            }
                    }
            }
            return Pair(resultsMap, orderRead)
        }
    }

    class Worker constructor(val id: String,
                             val data: ReceiveChannel<Duration>,
                             val results: SendChannel<Results>) {

        suspend fun process() {
            var counter = 0
            for (d in data) {
                // println("$id got value: $d")
                counter++
                delay(d)
            }
            // println("$id writing results")
            results.send(Results(id, counter))
        }
    }

    fun CoroutineScope.execute(messageCount: Int, workerCount: Int, biased: Boolean) {
        val data = Channel<Duration>()
        val results = List(workerCount) { Channel<Results>() }
        val boss = SelectReadBoss(messageCount, data, results)

        repeat(workerCount) { i ->
            launch {
                val id = "Worker-${i.toString().padStart((workerCount - 1).toString().length, '0')}"
                Worker(id, data, results[i]).process()
            }
        }

        launch {
            boss.generateData()
        }

        launch {
            val (resultsMap, orderRead) = boss.aggregateData(biased)
            println("\nBiased reads: $biased")
            println("Results map:")
            println(resultsMap)
            println("Order read:")
            println(orderRead)
        }
    }

    runBlocking {
        execute(1_000, 100, true)
    }

    runBlocking {
        execute(1_000, 100, false)
    }
}

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
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun main() {

    class Results(val id: String, val total: Int)

    class Boss constructor(val messageCount: Int,
                           val channel: List<SendChannel<Int>>,
                           val results: List<ReceiveChannel<Results>>) {
        suspend fun generateData(biased: Boolean) {
            repeat(messageCount) {
                val r = Random.nextInt()
                if (biased)
                    select<Unit> {
                        channel.onEach { it.onSend(r) {} }
                    }
                else
                    selectUnbiased<Unit> {
                        channel.onEach { it.onSend(r) {} }
                    }

                delay(10.milliseconds)
            }
            channel.onEach { it.close() }
        }

        suspend fun aggregateData(): Map<String, Int> {
            val resultsMap = mutableMapOf<String, Int>()
            while (resultsMap.size < results.size)
                select<Unit> {
                    results
                        .filter { !it.isClosedForReceive }
                        .onEach {
                            it.onReceiveOrClosed { value ->
                                if (!value.isClosed)
                                    resultsMap[value.value.id] = value.value.total
                            }
                        }
                }
            return resultsMap.toSortedMap()
        }
    }

    class Worker constructor(val id: String,
                             val channel: ReceiveChannel<Int>,
                             val results: SendChannel<Results>) {
        suspend fun process() {
            var counter = 0
            for (d in channel) {
                //println("$id got value: $d")
                counter++
                //delay(100.milliseconds)
            }
            // println("$id writing results")
            results.send(Results(id, counter))
        }
    }

    fun CoroutineScope.execute(messageCount: Int, workerCount: Int, biased: Boolean) {
        val data = List(workerCount) { Channel<Int>() }
        val results = List(workerCount) { Channel<Results>() }
        val boss = Boss(messageCount, data, results)

        repeat(workerCount) { i ->
            launch {
                val id = "Worker-${i.toString().padStart((workerCount - 1).toString().length, '0')}"
                Worker(id, data[i], results[i]).process()
            }
        }

        launch { boss.generateData(biased) }

        launch {
            println("\nBiased writes: $biased")
            println(boss.aggregateData())
        }
    }

    runBlocking {
        execute(1_000, 100, true)
    }

    runBlocking {
        execute(1_000, 100, false)
    }
}
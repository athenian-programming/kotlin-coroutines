package org.athenian

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun main() {

    class Results(val id: String, val total: Int)

    class Boss constructor(val messageCount: Int,
                           val data: List<SendChannel<Int>>,
                           val results: List<ReceiveChannel<Results>>) {

        suspend fun generateData() {
            repeat(messageCount) {
                val r = Random.nextInt()
                select<Unit> {
                    data.onEach { worker ->
                        worker.onSend(r) {}
                    }
                }
                delay(10.milliseconds)
            }
            data.onEach { it.close() }
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
                             val data: ReceiveChannel<Int>,
                             val results: SendChannel<Results>) {

        suspend fun process() {
            var counter = 0
            for (d in data) {
                println("$id got value: $d")
                counter++
                delay(100.milliseconds)
            }
            println("$id writing results")
            results.send(Results(id, counter))
        }
    }

    fun CoroutineScope.execute(messageCount: Int, workerCount: Int) {
        val data = List(workerCount) { Channel<Int>() }
        val results = List(workerCount) { Channel<Results>() }

        repeat(workerCount) { i ->
            launch {
                Worker(
                    "Worker-${i.toString().padStart((workerCount - 1).toString().length, '0')}",
                    data[i],
                    results[i]
                ).process()
            }
        }

        val boss = Boss(messageCount, data, results)

        launch {
            boss.generateData()
        }

        launch {
            println(boss.aggregateData())
        }
    }

    runBlocking {
        execute(1000, 100)
    }
}
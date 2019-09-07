package org.athenian

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.select
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

class Results(val id: Int, val total: Int)

@ExperimentalTime
class Boss constructor(
    val messageCount: Int,
    val data: SendChannel<Duration>,
    val results: List<ReceiveChannel<Results>>
) {
    @ExperimentalTime
    suspend fun generateData() {
        repeat(messageCount) {
            data.send(Random.nextInt(10).milliseconds)
            delay(Random.nextInt(5).milliseconds)
        }
        data.close()
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    suspend fun aggregateData(): Map<Int, Int> {
        val resultsMap = mutableMapOf<Int, Int>()
        while (resultsMap.size < results.size)
            select<Unit> {
                results
                    .filter { !it.isClosedForReceive }
                    .onEach {
                        it.onReceiveOrClosed { value ->
                            if (!value.isClosed) {
                                resultsMap[value.value.id] = value.value.total
                            }
                        }
                    }
            }
        return resultsMap
    }
}

@ExperimentalTime
class Worker constructor(val id: Int, val data: ReceiveChannel<Duration>, val results: SendChannel<Results>) {
    @ExperimentalTime
    suspend fun process() {
        var counter = 0
        for (d in data) {
            println("Worker $id got a value: $d")
            counter++
            delay(d)
        }
        println("Worker $id writing results")
        results.send(Results(id, counter))
    }
}

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun CoroutineScope.rockAndRoll(messageCount: Int, workerCount: Int) {
    val data = Channel<Duration>()
    val results = List(workerCount) { Channel<Results>() }

    List(workerCount) { i ->
        launch {
            val worker = Worker(i, data, results[i])
            worker.process()
        }
    }

    val boss = Boss(messageCount, data, results)

    launch {
        boss.generateData()
    }

    launch {
        val r = boss.aggregateData()
        println(r)
    }
}

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun main() {
    runBlocking {
        rockAndRoll(1000, 100)
    }
}
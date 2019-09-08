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

class Results1(val id: String, val total: Int)

@ExperimentalTime
class Boss1 constructor(
    val messageCount: Int,
    val data: SendChannel<Duration>,
    val results: List<ReceiveChannel<Results1>>
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
        return resultsMap
    }
}

@ExperimentalTime
class Worker1 constructor(val id: String, val data: ReceiveChannel<Duration>, val results: SendChannel<Results1>) {
    @ExperimentalTime
    suspend fun process() {
        var counter = 0
        for (d in data) {
            println("$id got value: $d")
            counter++
            delay(d)
        }
        println("$id writing results")
        results.send(Results1(id, counter))
    }
}

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun CoroutineScope.execute1(messageCount: Int, workerCount: Int) {
    val data = Channel<Duration>()
    val results = List(workerCount) { Channel<Results1>() }

    repeat(workerCount) { i ->
        launch {
            Worker1("Worker-$i", data, results[i]).process()
        }
    }

    val boss = Boss1(messageCount, data, results)

    launch {
        boss.generateData()
    }

    val v =
        async {
            boss.aggregateData()
        }

    launch {
        println(v.await())
    }
}

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun main() {
    runBlocking {
        execute1(1000, 100)
    }
}
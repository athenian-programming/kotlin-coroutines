package org.athenian

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.select
import kotlin.random.Random

class Results(val id: Int, val total: Int)

class Boss(val messageCount: Int, val data: SendChannel<Long>, val results: List<ReceiveChannel<Results>>) {
    suspend fun generateData() {
        repeat(messageCount) {
            data.send(Random.nextLong(10))
            delay(Random.nextLong(5))
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

class Worker(val id: Int, val data: ReceiveChannel<Long>, val results: SendChannel<Results>) {
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

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun CoroutineScope.rockAndRoll(messageCount: Int, workerCount: Int) {
    val data = Channel<Long>()
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

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun main() {
    runBlocking {
        rockAndRoll(1000, 100)
    }
}
package org.athenian

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.select
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

class Results2(val id: String, val total: Int)

@ExperimentalTime
class Boss2 constructor(
    val messageCount: Int,
    val data: List<SendChannel<Int>>,
    val results: List<ReceiveChannel<Results2>>
) {
    @ExperimentalTime
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
class Worker2 constructor(
    val id: String,
    val data: ReceiveChannel<Int>,
    val results: SendChannel<Results2>
) {
    @ExperimentalTime
    suspend fun process() {
        var counter = 0
        for (d in data) {
            println("$id got value: $d")
            counter++
            delay(100.milliseconds)
        }
        println("$id writing results")
        results.send(Results2(id, counter))
    }
}

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun CoroutineScope.execute2(messageCount: Int, workerCount: Int) {
    val data = List(workerCount) { Channel<Int>() }
    val results = List(workerCount) { Channel<Results2>() }

    repeat(workerCount) { i ->
        launch {
            Worker2("Worker-$i", data[i], results[i]).process()
        }
    }


    val boss = Boss2(messageCount, data, results)

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
        execute2(1000, 100)
    }
}
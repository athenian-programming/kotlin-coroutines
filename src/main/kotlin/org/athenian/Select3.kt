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

class Results3(val id: String, val total: Int)

@ExperimentalTime
class Boss3 constructor(
    val messageCount: Int,
    val slowWorker: SendChannel<Int>,
    val fastWorker: SendChannel<Int>,
    val results: List<ReceiveChannel<Results3>>
) {
    @ExperimentalTime
    suspend fun generateData() {
        repeat(messageCount) {
            val r = Random.nextInt()
            select<Unit> {
                slowWorker.onSend(r) { }
                fastWorker.onSend(r) { }
            }
            delay(10.milliseconds)
        }
        slowWorker.close()
        fastWorker.close()
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
class Worker3 constructor(
    val id: String,
    val delay: Duration,
    val data: ReceiveChannel<Int>,
    val results: SendChannel<Results3>
) {
    @ExperimentalTime
    suspend fun process() {
        var counter = 0
        for (d in data) {
            println("$id got value: $d")
            counter++
            delay(delay)
        }
        println("$id writing results")
        results.send(Results3(id, counter))
    }
}

@ExperimentalTime
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun CoroutineScope.execute3(messageCount: Int, slowDuration: Duration, fastDuration: Duration) {
    val slowData = Channel<Int>()
    val fastData = Channel<Int>()
    val results = List(2) { Channel<Results3>() }

    launch {
        Worker3("Slow Worker", slowDuration, slowData, results[0]).process()
    }

    launch {
        Worker3("Fast Worker", fastDuration, fastData, results[1]).process()
    }

    val boss = Boss3(messageCount, slowData, fastData, results)

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
        execute3(1000, 100.milliseconds, 10.milliseconds)
    }
}
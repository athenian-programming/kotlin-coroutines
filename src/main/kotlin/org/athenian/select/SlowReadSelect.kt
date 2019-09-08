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

    class Boss constructor(val messageCount: Int,
                           val slowWorker: SendChannel<Int>,
                           val fastWorker: SendChannel<Int>,
                           val results: List<ReceiveChannel<Results>>) {

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
                             val delay: Duration,
                             val data: ReceiveChannel<Int>,
                             val results: SendChannel<Results>) {

        suspend fun process() {
            var counter = 0
            for (d in data) {
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

        launch {
            Worker("Slow Worker", slowDuration, slowData, results[0]).process()
        }

        launch {
            Worker("Fast Worker", fastDuration, fastData, results[1]).process()
        }

        val boss = Boss(messageCount, slowData, fastData, results)

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
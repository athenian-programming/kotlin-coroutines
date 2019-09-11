package org.athenian.select

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.athenian.delay
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@ExperimentalTime
fun main() {

    class MutexWrapper(val id: Int, val channel: Channel<Int>, val mutex: Mutex, val action: suspend () -> Unit)

    class Worker(mutexCount: Int) {

        val wrappers = mutableListOf<MutexWrapper>()

        init {
            repeat(mutexCount) { i ->
                val c = Channel<Int>()
                val m = Mutex()
                wrappers += MutexWrapper(i, c, m) {
                    var active = true
                    while (active) {
                        m.withLock {
                            println("Acquired lock for $i")
                            val v = c.receiveOrClosed()
                            if (v.isClosed) {
                                active = false
                            } else {
                                println("Unlocking $i")
                            }
                        }
                    }
                    println("Completed action for $i")
                }
            }
        }

        suspend fun selectMutexes(iterationCount: Int) {

            val mutexOrder = mutableListOf<Int>()

            repeat(iterationCount) {
                // println("Selecting...")
                val selected =
                    select<MutexWrapper> {
                        wrappers
                            .onEach { wrapper ->
                                wrapper.mutex.onLock { mutex ->
                                    println("Selected: ${wrapper.id} ${mutex}")
                                    wrapper
                                }
                            }
                    }
                println("Unlocking ${selected.id}")
                selected.mutex.unlock()
                mutexOrder.add(selected.id)
            }

            delay(50.milliseconds)
            println("Mutex order: $mutexOrder")
        }
    }

    runBlocking {
        val iterationCount = 3
        val workerCount = 5
        val worker = Worker(workerCount)

        worker.wrappers
            .forEach {
                launch {
                    it.action()
                }
            }

        val j =
            launch {
                worker.selectMutexes(iterationCount)
                delay(50.milliseconds)
            }

        repeat(iterationCount) {
            worker.wrappers[Random.nextInt(workerCount)].channel.send(Random.nextInt())
        }

        //j.join()

        repeat(workerCount) { i ->
            worker.wrappers[i].channel.close()
        }

    }
}
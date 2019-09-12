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

    class Worker(val mutexCount: Int) {

        val wrappers = mutableListOf<MutexWrapper>()
        val mutexOrder = mutableListOf<Int>()

        init {
            repeat(mutexCount) { i ->
                val c = Channel<Int>()
                val m = Mutex()
                wrappers += MutexWrapper(i, c, m) {
                    var active = true
                    while (active) {
                        m.withLock {
                            println("Acquired lock for: $i")
                            val v = c.receiveOrClosed()
                            if (v.isClosed) {
                                active = false
                            } else {
                                println("Surrendering lock for: $i")
                            }
                        }
                        delay(50.milliseconds)
                    }
                    println("Completed action for $i")
                }
            }
        }

        suspend fun selectMutexes(iterationCount: Int) {

            repeat(iterationCount) {
                val selected =
                    select<MutexWrapper> {
                        wrappers
                            .onEach { wrapper ->
                                wrapper.mutex.onLock { mutex ->
                                    println("Locked on select: ${wrapper.id}")
                                    wrapper
                                }
                            }
                    }

                println("Unlocked on select: ${selected.id}")
                selected.mutex.unlock()
                mutexOrder.add(selected.id)
                delay(50.milliseconds)
            }
        }
    }

    val iterationCount = 200
    val mutexCount = 100
    val worker = Worker(mutexCount)

    runBlocking {

        worker.wrappers
            .forEach { wrapper ->
                launch {
                    wrapper.action.invoke()
                }
            }

        launch {
            worker.selectMutexes(iterationCount)
            delay(50.milliseconds)
        }

        delay(50.milliseconds)

        repeat(iterationCount) { i ->
            val v = Random.nextInt(mutexCount)
            println("Choosing to unlock: $v")
            worker.wrappers[v].channel.send(1)
            delay(50.milliseconds)
        }

        repeat(mutexCount) { i ->
            worker.wrappers[i].channel.close()
        }

    }

    println("Size: ${worker.mutexOrder.size} Mutex order: ${worker.mutexOrder}")

}
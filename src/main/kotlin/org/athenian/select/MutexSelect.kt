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
    class MutexWrapper(val id: Int, val channel: Channel<Unit>, val mutex: Mutex, val block: suspend () -> Unit)

    val mutexCount = 100
    val iterationCount = 200

    val mutexOrder = mutableListOf<Int>()
    val randomVals = List(iterationCount) { Random.nextInt(mutexCount) }

    val wrappers =
        List(mutexCount) { i ->
            val c = Channel<Unit>()
            val m = Mutex()
            MutexWrapper(i, c, m) {
                var active = true
                while (active) {
                    m.withLock {
                        println("Block acquired lock for: $i")
                        val v = c.receiveOrClosed()
                        active = !v.isClosed
                        if (active)
                            println("Block surrendered lock for: $i")
                    }
                    delay(50.milliseconds)
                }
                println("Completed block for $i")
            }
        }

    suspend fun selectMutex(iterationCount: Int) {
        val selected =
            select<MutexWrapper> {
                wrappers
                    .onEach { wrapper ->
                        wrapper.mutex.onLock { mutex ->
                            println("selectMutex acquired lock for: ${wrapper.id}")
                            wrapper
                        }
                    }
            }

        mutexOrder += selected.id
        println("selectMutex surrendered lock for: ${selected.id}")
        selected.mutex.unlock()
        delay(50.milliseconds)
    }

    runBlocking {
        // Start the actions of each of the wrappers in a corputine
        wrappers.forEach { launch { it.block.invoke() } }

        // Repeatedly select a mutex in a coroutine
        launch { repeat(iterationCount) { selectMutex(iterationCount) } }

        // Give coroutines a chance to get setup
        delay(50.milliseconds)

        // Send msg to unlock random mutex
        randomVals.onEach { i ->
            println("Choosing to unlock: $i")
            wrappers[i].channel.send(Unit)
            delay(100.milliseconds)
        }

        // Stop coroutines
        wrappers.onEach { it.channel.close() }
    }

    // Report results
    println("\nSize: ${mutexOrder.size} \nMatching: ${mutexOrder == randomVals} \nMutex order: ${mutexOrder}")
}
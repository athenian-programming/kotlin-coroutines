package org.athenian.select

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.athenian.delay
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@ExperimentalTime
fun main() {

    class MutexWrapper(val id: Int, val mutext: Mutex = Mutex())

    class Worker(val count: Int) {

        val mutexes = List(count) { i -> MutexWrapper(i) }

        suspend fun lock(id: Int) {
            println("Locking: $id ${mutexes[id].mutext}")
            mutexes[id].mutext.withLock {
                delay(1.seconds)
            }
        }

        fun unlock(id: Int) {
            println("Unlocking: $id ${mutexes[id].mutext}")
            mutexes[id].mutext.unlock()
        }

        suspend fun selectMutexes() {

            val orderLocked = mutableListOf<Int>()

            println("Locking prior to selects:")
            mutexes.onEach {
                it.mutext.lock()
                println("${it.id} ${it.mutext}")
            }

            repeat(count) {
                println("Selecting...")
                val selected =
                    select<Int> {
                        mutexes.onEach {
                            it.mutext.onLock { mutex ->
                                println("Selected: ${it.id} ${mutex}")
                                it.id
                            }
                        }
                    }
                orderLocked.add(selected)
            }

            println(orderLocked)
        }
    }

    runBlocking {
        val count = 5
        val worker = Worker(count)

        launch {
            worker.selectMutexes()
        }

        launch {
            repeat(count) {
                worker.unlock(Random.nextInt(count))
                delay(50.milliseconds)
            }
        }
    }
}
package org.athenian

import kotlinx.coroutines.*
import java.util.concurrent.CancellationException

val Job.status: String
    get() = "Active: ${this.isActive} Completed: ${this.isCompleted} Cancelled: ${this.isCancelled} Children: ${this.children.count()}"

@InternalCoroutinesApi
fun main() {
    runBlocking {
        val outer = launch {

            val innerinner = launch {
                delay(10000)
            }

            delay(100)
            val inner = coroutineContext[Job]!!
            println("inner status: ${inner.status}")
            inner.cancel(CancellationException("Test cancel"))
            println("inner status: ${inner.status}")

            // Manually check for cancellation of call a suspending function
            // if (!inner.isActive) return@launch
            // delay(1)

            println("Should not get here")
        }

        delay(200)
        println("outer status: ${outer.status}")
        println("Cancellation exception: ${outer.getCancellationException()}")
    }

    println("Done")
}
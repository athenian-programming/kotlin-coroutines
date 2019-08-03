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
            log("inner status: ${inner.status}")
            inner.cancel(CancellationException("Test cancel"))
            log("inner status: ${inner.status}")

            // Manually check for cancellation of call a suspending function
            // if (!inner.isActive) return@launch
            // delay(1)

            log("Should not get here")
        }

        delay(200)
        log("outer status: ${outer.status}")
        log("Cancellation exception: ${outer.getCancellationException()}")
    }

    log("Done")
}
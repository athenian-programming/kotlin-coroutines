package org.athenian

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CancellationException
import kotlin.time.milliseconds
import kotlin.time.seconds

val Job.status: String
    get() = "Active: ${this.isActive} Completed: ${this.isCompleted} Cancelled: ${this.isCancelled} Children: ${this.children.count()}"

fun main() {
    runBlocking {
        val outer =
            launch {

                launch {
                    delay(10.seconds)
                }

                delay(100.milliseconds)
                val inner = coroutineContext[Job]!!
                log("inner status: ${inner.status}")
                inner.cancel(CancellationException("Test cancel"))
                log("inner status: ${inner.status}")

                // Manually check for cancellation of call a suspending function
                // if (!inner.isActive) return@launch
                // delay(1.seconds)

                log("Should not get here")
            }

        delay(200.milliseconds)
        log("outer status: ${outer.status}")
        log("Cancellation exception: ${outer.getCancellationException()}")
    }
    log("Done")
}
package org.athenian.async

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.athenian.delay
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@ExperimentalTime
fun main() {
    suspend fun calc(): String {
        delay(3.seconds)
        return "A string value"
    }

    runBlocking {
        val calcCall = async(Dispatchers.Default) { calc() }

        while (true) {
            val completed =
                withTimeoutOrNull(500.milliseconds.toLongMilliseconds()) {
                    println("Waiting")
                    println("Got back: ${calcCall.await()}")
                }

            if (completed == null)
                println("Timed out")
            else
                break
        }
    }
}
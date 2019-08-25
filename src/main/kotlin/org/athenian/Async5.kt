package org.athenian

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

fun main() {

    fun calc(): String {
        Thread.sleep(3_000)
        return "A string value"
    }

    runBlocking {

        val calcCall = async(Dispatchers.Default) { calc() }

        while (true) {
            val completed =
                withTimeoutOrNull(500) {
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
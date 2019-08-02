package org.athenian

import kotlinx.coroutines.*

fun main() {

    val mult = { a: Int, b: Int ->
        println("Calculating value")
        a * b
    }

    val deferred1 = GlobalScope.async { mult(4, 6) }
    val deferred2 = GlobalScope.async(start = CoroutineStart.LAZY) { mult(7, 9) }

    runBlocking {
        delay(100)
        println("Deferred1")
        println(deferred1.await())

        println("Deferred2")
        println(deferred2.await())
    }
}
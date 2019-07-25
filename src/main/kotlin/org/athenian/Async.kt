package org.athenian

import kotlinx.coroutines.*


fun main() {
    val i = 5

    println("Assigning deferred0")
    val deferred0 =
        (1..i)
            .map {
                println("Mapping $it in deferred0")
                it
            }
            .onEach { println("Completed deferred0") }
    println("Summing deferred0")
    println("Sum of deferred0: ${deferred0.sumBy { it }}")

    println("\nAssigning deferred1")
    val deferred1 =
        (1..i)
            .asSequence()
            .map {
                println("Mapping $it in deferred1")
                it
            }
            .onEach { println("Completed deferred1") }
    println("Summing deferred1")
    println("Sum of deferred1: ${deferred1.sumBy { it }}")

    println("\nAssigning deferred2")
    val deferred2 =
        (1..i)
            .map {
                println("Mapping $it in deferred2")
                GlobalScope.async {
                    println("Returning $it in deferred2")
                    it
                }
            }
            .onEach { println("Completed deferred2") }
    runBlocking {
        delay(100)
        println("Summing deferred2")
        println("Sum of deferred2: ${deferred2.sumBy { it.await() }}")
    }

    println("\nAssigning deferred3")
    val deferred3 =
        (1..i)
            .map {
                println("Mapping $it in deferred3")
                GlobalScope.async(start = CoroutineStart.LAZY) {
                    println("Returning $it in deferred3")
                    it
                }
            }
            .onEach { println("Completed deferred3") }
    runBlocking {
        delay(100)
        println("Summing deferred3")
        println("Sum of deferred3: ${deferred3.sumBy { it.await() }}")
    }
}
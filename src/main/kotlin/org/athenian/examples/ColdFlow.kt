package org.athenian.examples

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking

fun main() {

  val ints: Flow<Int> =
    flow {
      for (i in 1..10) {
        delay(1000)
        println("Emitting $i")
        emit(i)
      }
    }

  runBlocking {
    println("Waiting to start")
    delay(3_000)
    ints
      .take(5)
      .collect {
        println("Read $it")
      }
  }
}
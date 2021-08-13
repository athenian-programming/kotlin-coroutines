package org.athenian

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main() {
  runBlocking {
    val deferred: Deferred<String> = async {
      return@async "${Thread.currentThread()} has run."
    }

    println("Waiting for async to complete")
    println(deferred.await())
  }

  println("Exited runBlocking")
}
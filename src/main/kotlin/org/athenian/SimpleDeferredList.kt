package org.athenian

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main() {
  runBlocking {
    val deferreds: List<Deferred<String>> =
      List(10) {
        async {
          return@async "${Thread.currentThread()} has run."
        }
      }

    println("Waiting for async to complete")
    deferreds.forEach { println(it.await()) }
  }

  println("Exited runBlocking")
}
package org.athenian

import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// From https://kt.academy/article/cc-suspension

suspend fun main() {
  println("Before")

  suspendCoroutine<Unit> { continuation ->
    thread {
      println("Suspended")
      Thread.sleep(1000)
      continuation.resume(Unit)
      println("Resumed")
    }
  }

  println("After")
}

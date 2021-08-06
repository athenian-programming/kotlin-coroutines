package org.athenian.async

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration

fun main() {
  val mult = { a: Int, b: Int ->
    log("Calculating value")
    a * b
  }

  val deferred1 = GlobalScope.async { mult(4, 6) }
  val deferred2 = GlobalScope.async(start = CoroutineStart.LAZY) { mult(7, 9) }

  runBlocking {
    delay(Duration.milliseconds(100))
    log("Deferred1")
    log(deferred1.await())

    log("Deferred2")
    log(deferred2.await())
  }
}
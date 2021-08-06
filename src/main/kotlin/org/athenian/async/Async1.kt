package org.athenian.async

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration
import kotlin.time.measureTimedValue

fun main() {

  suspend fun calc(): String {
    delay(Duration.seconds(3))
    return "A string val"
  }

  runBlocking {
    val (val1, dur1) =
      measureTimedValue {
        val a = calc()
        val b = calc()
        listOf(a, b)
      }
    log("Vals = $val1 took $dur1")

    val (val2, dur2) =
      measureTimedValue {
        val a = async { calc() }
        val b = async { calc() }
        println("Vals prior to .await() = ${listOf(a, b)}")
        listOf(a.await(), b.await())
      }
    log("Vals = $val2 took $dur2")

    val (val3, dur3) =
      measureTimedValue {
        val a = async(start = CoroutineStart.LAZY) { calc() }
        val b = async(start = CoroutineStart.LAZY) { calc() }
        listOf(a.await(), b.await())
      }
    log("Vals = $val3 took $dur3")
  }
}

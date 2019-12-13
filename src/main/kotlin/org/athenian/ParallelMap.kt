package org.athenian

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue
import kotlin.time.milliseconds

// See: https://jivimberg.io/blog/2018/05/04/parallel-map-in-kotlin/

fun main() {
  val iterations = 10

  suspend fun someCalculation(v: Int): String {
    val del = 1_000.milliseconds
    delay(del)
    println("$v paused $del")
    return "Value $v"
  }

  // Long version
  runBlocking {
    val (vals, dur) =
      measureTimedValue {
        (1..iterations)
          .map {
            async {
              someCalculation(it)
            }
          }
          .awaitAll()
      }
    println("Calculated in $dur vals: $vals")
  }

  // Wrap the boilerplate code with an extension function
  suspend fun <A, B> Iterable<A>.pmap(block: suspend (A) -> B): List<B> =
    coroutineScope {
      map { async { block(it) } }.awaitAll()
    }

  // Concise version
  runBlocking {
    val (vals, dur) =
      measureTimedValue {
        (1..iterations)
          .pmap {
            someCalculation(it)
          }
      }
    println("Calculated in $dur vals: $vals")
  }
}
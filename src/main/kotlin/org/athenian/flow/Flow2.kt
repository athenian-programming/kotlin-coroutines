package org.athenian.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.athenian.delay
import org.athenian.log
import kotlin.time.Duration

// See https://proandroiddev.com/an-early-look-at-kotlin-coroutines-flow-62e46baa6eb0

fun main() {
  fun flowExample() {
    val intVals =
      flow {
        var i = 0
        while (true) {
          log("Emitting flowExample $i")
          emit(i++)
        }
      }

    runBlocking {
      intVals
        .take(5)
        .map { it * it }
        .onEach { delay(Duration.milliseconds(100)) }
        .collect { log("Collecting flowExample $it") }
    }
  }

  fun asFlowExample() =
    runBlocking {
      log()
      (1..100)
        .asFlow()
        .onStart { log("Starting asFlowExample") }
        .take(5)
        .map { it * it }
        .onEach { log("First asFlowExample onEach()") }
        .onEach { delay(Duration.milliseconds(100)) }
        .flowOn(Dispatchers.Default) //changes upstream context
        .onEach { log("Second asFlowExample onEach()") }
        .map { it * 2 }
        .onCompletion { log("Finished asFlowExample") }
        .collect { log("Collecting asFlowExample $it") }

    }

  fun flowOfExample() =
    runBlocking {
      log()
      flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .onStart { log("Starting flowOfExample") }
        .take(5)
        .map { it * it }
        .onEach { log("First flowOfExample onEach()") }
        .onEach { delay(Duration.milliseconds(100)) }
        .flowOn(Dispatchers.Default) //changes upstream context
        .onEach { log("Second flowOfExample onEach()") }
        .map { it * 2 }
        .onCompletion { log("Finished flowOfExample") }
        .collect { log("Collecting flowOfExample $it") }
    }

  flowExample()
  asFlowExample()
  flowOfExample()
  asFlowExample()
}
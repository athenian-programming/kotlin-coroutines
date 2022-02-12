package org.athenian.scan

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

// See https://kt.academy/article/cc-scan

object Scan2 {
  fun <T, R> Flow<T>.myScan(
    initial: R,
    operation: suspend (accumulator: R, value: T) -> R
  ): Flow<R> = flow {
    var accumulator: R = initial
    emit(accumulator)
    collect { value ->
      accumulator = operation(accumulator, value)
      emit(accumulator)
    }
  }

  @JvmStatic
  fun main(args: Array<String>) {
    runBlocking {
      flowOf(1, 2, 3, 4).onEach { delay(1000) }.myScan(0) { acc, v -> acc + v }.collect { println(it) }
    }
  }
}
package org.athenian.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.athenian.log

// See https://proandroiddev.com/an-early-look-at-kotlin-coroutines-flow-62e46baa6eb0
// Custom flow operators

fun main() {
  fun <T> Flow<T>.everyOther(): Flow<T> =
    flow {
      var skip = false
      collect { value ->
        if (!skip)
          emit(value)
        skip = !skip
      }

    }

  fun <T> Flow<T>.everyNth(inc: Int): Flow<T> =
    flow {
      var counter = 0
      collect { value ->
        if (counter % inc == 0)
          emit(value)
        counter++
      }
    }

  val vals = flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

  runBlocking {
    vals.everyOther().collect { log(it) }
    log()
    vals.everyNth(3).collect { log(it) }
  }
}


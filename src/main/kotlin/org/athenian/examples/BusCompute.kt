package org.athenian.examples

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// https://elizarov.medium.com/shared-flows-broadcast-channels-899b675e805c

fun main() {
  runBlocking {
    val sharedFlow = MutableSharedFlow<Int>()
    val results = Channel<String>()

    launch {
      for (result in results)
        println("Got value $result")
    }

    launch {
      sharedFlow.onStart { println("Starting square receiver") }
        .takeWhile { it != -1 }
        .onCompletion { results.close() }
        .collect {
          //println("Read square value: $it")
          results.send("$it squared id ${it * it}")
        }
    }

    launch {
      sharedFlow.onStart { println("Starting cube receiver") }
        .takeWhile { it != -1 }
        .collect {
          //println("Read cube value: $it")
          results.send("$it cubed id ${it * it * it}")
        }
    }

    launch {
      repeat(5) { i ->
        sharedFlow.emit(i)
      }
      sharedFlow.emit(-1)
    }
  }
}
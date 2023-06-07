package org.athenian.select

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex

fun main() {
  val mutex0 = Mutex(true)
  val mutex1 = Mutex(true)
  val mutex2 = Mutex(true)

  runBlocking {
    launch {
      repeat(3) {
        val selected =
          select<Pair<Mutex, String>> {}
        println("Selected: $selected")
      }
    }

    println("Unlocking: mutex2")
    mutex2.unlock()

    delay(100)
    println("Unlocking: mutex1")
    mutex1.unlock()

    delay(100)
    println("Unlocking: mutex0")
    mutex0.unlock()

    coroutineContext.cancelChildren()
  }
}
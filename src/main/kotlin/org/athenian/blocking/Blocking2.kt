package org.athenian.blocking

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.athenian.delay
import org.athenian.log
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

fun main() {
  suspend fun sleepingCall(context: CoroutineContext) {
    withContext(context) {
      log("sleeping")
      Thread.sleep(3_000)
    }
  }

  suspend fun delayingCall() {
    log("delaying")
    delay(seconds(3))
  }

  Executors.newFixedThreadPool(20).asCoroutineDispatcher()
    .use { poolDispatcher ->
      for (count in listOf(8, 9, 16, 17)) {
        measureTime {
          runBlocking {
            repeat(count) {
              launch(CoroutineName("Dispatchers.Default-item-$it")) {
                sleepingCall(Dispatchers.Default)
              }
            }
          }
        }.also {
          log("Total time for $count calls of sleepingCalls with Dispatchers.Default: $it\n")
        }

        measureTime {
          runBlocking {
            repeat(count) {
              launch(CoroutineName("poolDispatcher-item-$it")) {
                sleepingCall(poolDispatcher)
              }
            }
          }
        }.also { log("Total time for $count calls of sleepingCalls with poolDispatcher: $it\n") }

        measureTime {
          runBlocking {
            repeat(count) {
              launch(CoroutineName("delaying-item-$it")) {
                delayingCall()
              }
            }
          }
        }.also { log("Total time for $count calls of delayingCalls: $it\n") }
      }
    }
}

package org.athenian

import kotlin.concurrent.thread
import kotlin.time.measureTime

fun main() {

  fun daemonThread() {
    val t =
      thread(isDaemon = true) {
        log("Before sleep")
        Thread.sleep(2000)
        log("After sleep")
      }

    log("Before waiting for thread")
    t.join()
    log("After waiting for thread")
  }

  fun delayedThread() {
    val threads =
      List(10) {
        thread(start = false, name = "MyThread-$it") {
          log("Before sleep in $it")
          Thread.sleep(2_000)
          log("After sleep in $it")
        }
      }

    log("Before starting threads")
    threads.forEach { it.start() }
    log("After starting threads")

    log("Before waiting for threads")
    threads.forEach { it.join() }
    log("After waiting for threads")
  }

  fun maxThreadsAtOnce() {
    measureTime {
      val threads =
        List(12800) {
          thread {
            log("Started")
            Thread.sleep(1_000)
            log("Done")
          }
        }

      threads.forEach { it.join() }
    }.also { log("Total time: $it") }
  }

  //daemonThread()
  //delayedThread()
  maxThreadsAtOnce()
}
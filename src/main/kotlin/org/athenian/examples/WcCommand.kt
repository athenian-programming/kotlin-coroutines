package org.athenian.examples

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun runCommand(cmd: String, workingDir: String = ".") =
  try {
    val parts = cmd.split("\\s".toRegex())
    ProcessBuilder(*parts.toTypedArray())
      .directory(File(workingDir))
      .redirectOutput(ProcessBuilder.Redirect.PIPE)
      .redirectError(ProcessBuilder.Redirect.PIPE)
      .start()
      .run {
        waitFor(60, TimeUnit.MINUTES)
        inputStream.bufferedReader().readText().split("\n")
      }
  } catch (e: IOException) {
    e.printStackTrace()
    emptyList()
  }

fun main() {

  data class Contents(val words: Int, val chars: Int)

  val charCount = AtomicInteger(0)
  val wordCount = AtomicInteger(0)
  val lineCount = AtomicInteger(0)

  runBlocking {
    val workerCount = 5
    val latch = CountDownLatch(workerCount)
    val fnameChannel = Channel<String>()
    val calcChannel = Channel<Contents>()

    launch {
      for (calc in calcChannel) {
        charCount.addAndGet(calc.chars)
        wordCount.addAndGet(calc.words)
        lineCount.incrementAndGet()
      }
    }

    repeat(workerCount) {
      launch {
        for (filename in fnameChannel) {
          println("$it $filename")
          if (filename.endsWith(".txt")) {
            File(filename).inputStream().bufferedReader().useLines { lines ->
              lines.forEach { line ->
                calcChannel.send(Contents(line.split("\\s+".toRegex()).count(), line.length + 1))
              }
            }
          }
        }
        println("Finished wc worker")
        latch.countDown()
        if (latch.count == 0L)
          calcChannel.close()
      }
    }

    launch {
      println("Running command")
      runCommand("ls")
        .forEach { line ->
          println("Sending")
          fnameChannel.send(line)
        }
      fnameChannel.close()
    }
  }

  println("${lineCount.get()} ${wordCount.get()} ${charCount.get()}")
}
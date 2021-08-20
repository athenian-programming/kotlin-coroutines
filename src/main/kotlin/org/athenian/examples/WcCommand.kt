package org.athenian.examples

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

// https://elizarov.medium.com/shared-flows-broadcast-channels-899b675e805c

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

  val DONE = Contents(-1, -1)

  val charCount = AtomicInteger(0)
  val wordCount = AtomicInteger(0)
  val lineCount = AtomicInteger(0)

  runBlocking {
    val workerCount = 5
    val fnameChannel = Channel<String>()
    val calcChannel = Channel<Contents>()

    launch {
      var count = 0
      for (calc in calcChannel) {
        if (calc == DONE) {
          count++
          if (count == workerCount)
            calcChannel.close()
        } else {
          charCount.addAndGet(calc.chars)
          wordCount.addAndGet(calc.words)
          lineCount.incrementAndGet()
        }
      }
    }

    repeat(workerCount) {
      launch {
        for (filename in fnameChannel) {
          //println("$it $filename")
          if (filename.endsWith(".txt")) {
            File(filename).inputStream().bufferedReader().useLines { lines ->
              lines.forEach { line ->
                val words = line.split("\\s+".toRegex()).count()
                val chars = line.length + 1
                calcChannel.send(Contents(words, chars))
              }
            }
          }
        }
        calcChannel.send(DONE)
      }
    }

    launch {
      runCommand("ls")
        .forEach { line ->
          println("Sending $line")
          fnameChannel.send(line)
        }
      fnameChannel.close()
    }
  }

  println("${lineCount.get()} ${wordCount.get()} ${charCount.get()}")
}
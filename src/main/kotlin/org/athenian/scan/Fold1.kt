package org.athenian.scan

// See https://kt.academy/article/cc-scan

object Fold1 {
  @JvmStatic
  fun main(args: Array<String>) {
    val list = listOf(1, 2, 3, 4)

    val res = list.fold(0) { acc, i -> acc + i }
    println(res) // 10

    val res2 = list.fold(1) { acc, i -> acc * i }
    println(res2) // 24
  }
}
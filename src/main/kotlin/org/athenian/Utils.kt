package org.athenian

fun getThreadName() = Thread.currentThread().name

// Run with -Dkotlinx.coroutines.debug to see custom coroutine names
fun log(msg: String = "") = println("[${Thread.currentThread().name}] $msg")

fun log(obj: Any) = log(obj.toString())

fun log(block: () -> String) = log(block())

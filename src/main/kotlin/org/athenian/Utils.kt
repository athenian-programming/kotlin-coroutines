package org.athenian

import kotlinx.coroutines.delay
import kotlin.time.Duration

fun getThreadName(): String = Thread.currentThread().name

// Run with -Dkotlinx.coroutines.debug to see custom coroutine names
fun log(msg: String = "") = println("[${getThreadName()}] $msg")

fun log(obj: Any) = log(obj.toString())

fun log(block: () -> String) = log(block())

suspend fun delay(duration: Duration) = delay(duration.inWholeMilliseconds)


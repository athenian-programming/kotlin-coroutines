package org.athenian

fun getThreadName() = Thread.currentThread().name

fun log(msg: String = "") = println("[${Thread.currentThread().name}] $msg")

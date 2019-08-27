package org.athenian

class ByteCodeDifference {
    fun first(): Int {
        return 2
    }

    suspend fun second(): Int {
        return 2
    }
}

/*
kotlinc-jvm src/main/kotlin/org/athenian/ByteCodeDifference.kt
javap -c org.athenian.ByteCodeDifference

See the difference between first and second in the bytecode
The second method takes a continuation as parameter
*/
package org.athenian

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

// Custom flow operators

fun main() {
    val vals = flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    runBlocking {
        vals
            .everyOther()
            .collect { log(it) }
        log()
        vals
            .everyNth(3)
            .collect { log(it) }
    }
}

public fun <T> Flow<T>.everyOther(): Flow<T> =
    flow {
        var skip = false
        collect { value ->
            if (!skip)
                emit(value)
            skip = !skip
        }

    }

public fun <T> Flow<T>.everyNth(inc: Int): Flow<T> =
    flow {
        var counter = 0
        collect { value ->
            if (counter % inc == 0)
                emit(value)
            counter++
        }

    }


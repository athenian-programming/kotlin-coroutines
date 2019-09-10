package org.athenian.select

import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.selects.selectUnbiased
import org.athenian.delay
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
fun main() {

    data class JobInfo(val id: Int, val job: Job, var joined: Boolean = false)

    class Worker(val count: Int) {

        suspend fun execute(biased: Boolean) {

            val orderJoined = mutableListOf<Int>()

            coroutineScope {

                val jobs =
                    List(count) { i ->
                        JobInfo(i, launch { delay(1.seconds) })
                    }

                repeat(jobs.size) {
                    val selected =
                        if (biased)
                            select<JobInfo> {
                                jobs.filter { !it.joined }
                                    .onEach { jobInfo -> jobInfo.job.onJoin { jobInfo } }
                            }
                        else
                            selectUnbiased {
                                jobs.filter { !it.joined }
                                    .onEach { jobInfo -> jobInfo.job.onJoin { jobInfo } }
                            }
                    orderJoined.add(selected.id)
                    selected.joined = true
                }
            }

            println("\nBiased: $biased")
            println(orderJoined)
        }

    }

    runBlocking {
        val worker = Worker(100)

        async { worker.execute(true) }.await()
        async { worker.execute(false) }.await()
    }
}
package ch13.example00

import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.testng.annotations.Test
import java.lang.System.*

class CoroutineBuilderExample {
    @Test
    fun coroutineLaunch(): Unit {
        val time = currentTimeMillis()

        GlobalScope.launch {
            delay(100)
            println("Task 1 finished in ${currentTimeMillis() - time}ms")
        }

        GlobalScope.launch {
            delay(100)
            println("Task 2 finished in ${currentTimeMillis() - time}ms")
        }

        Thread.sleep(200)
        /**
         * Task 2 finished in 128ms
         * Task 1 finished in 128ms
         */
    }

    @Test(testName = "async")
    suspend fun coroutineAsync() {
            val message = GlobalScope.async {
                delay(100)
                "abc"
            }

            val count = GlobalScope.async {
                delay(100)
                1 + 2
            }

            print("delay")
            delay(500)

            val result = message.await().repeat(count.await())
            println(result)
    }

    @Test
    fun coroutineRunBlocking() {
        GlobalScope.launch {
            delay(100)
            println("Background task: ${Thread.currentThread().name}")
        }

        runBlocking {
            println("Primary task: ${Thread.currentThread().name}")
            delay(200)
        }
        //Primary task: main @coroutine#2
        //Background task: DefaultDispatcher-worker-1 @coroutine#1
    }
    @Test
    fun scope () {
        runBlocking {
            println("Parent task started")

            launch {
                println("Task A started")
                delay(200)
                println("Task A finished")
            }

            launch {
                println("Task B started")
                delay(200)
                println("Task B finished")
            }

            delay(100)
            println("Parent task finished")
        }

        println("Shutting down")
        //Parent task started
        //Task A started
        //Task B started
        //Parent task finished
        //Task A finished
        //Task B finished
        //Shutting down
    }
    @Test
    fun scopeCoroutineScope () {
        runBlocking {
            println("Parent task started")

            coroutineScope {
                launch {
                    println("Task A started")
                    delay(200)
                    println("Task A finished")
                }

                launch {
                    println("Task B started")
                    delay(200)
                    println("Task B finished")
                }
            }

            println("Parent task finished")
        }

        println("Shutting down")
        //Parent task started
        //Task A started
        //Task B started
        //Task A finished
        //Task B finished
        //Parent task finished
        //Shutting down
    }

    @Test
    fun coroutineContext() {
        GlobalScope.launch {
            println("Task is active: ${coroutineContext[Job.Key]!!.isActive}")
        }
        Thread.sleep(100)

        //Task is active: true
    }

}
package ch13.example00

import kotlinx.coroutines.*
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
}
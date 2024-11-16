package ch13.example00

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.testng.annotations.Test
import java.lang.System.*

class CoroutineBuilderExample {
    @Test
    fun coroutine(): Unit {
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
}
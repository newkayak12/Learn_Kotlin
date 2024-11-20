package ch13.example00

import kotlinx.coroutines.*
import org.testng.annotations.Test

class CoroutineFlowControlAndJobLifeCycle {
    @Test
    fun coroutineFlow() {
        runBlocking {
            val job =launch(start=CoroutineStart.LAZY) {
                println("JobStarted")
            }
            
            delay(100)


            println("Preparing to start...")
            job.start();
        }
        /**
         * Preparing to start...
         * JobStarted
         *
         */
    }

    @Test
    fun coroutineContext() {
        runBlocking {
            val job = coroutineContext[Job.Key]!!

            launch { println("This is task A") }
            launch { println("This is task B") }

            println("${job.children.count()} children running")
        }
        //2 children running
        //This is task A
        //This is task B
    }

    @Test
    fun coroutineCancel() {
        runBlocking {
            val squarePrinter = GlobalScope.launch(Dispatchers.Default){
                var i = 1
                while ( true ) {
                    println(i++)
                }
            }

            delay(100)
            squarePrinter.cancel()
        }
    }

    @Test
    fun coroutineParent () {
        runBlocking {
            val parentJob = launch {
                println("Parent started")

                launch {
                    println("Child 1 started")
                    delay(500)
                    println("Child 1 completed")
                }

                launch {
                    println("Child 2 started")
                    delay(500)
                    println("Child 2 completed")
                }

                delay(500)
                println("Parent completed")
            }

            delay(100)
            parentJob.cancel()
        }

        //Parent started
        //Child 1 started
        //Child 2 started
    }
}
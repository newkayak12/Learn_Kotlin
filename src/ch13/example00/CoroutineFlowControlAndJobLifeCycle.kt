package ch13.example00

import kotlinx.coroutines.*
import org.testng.annotations.Test
import java.io.File

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


    @Test
    fun coroutineTimeout () {
        runBlocking {
            val asyncData = async { File("./README.md").readText() }
            try {

                val text = withTimeout(1) { asyncData.await() }
                println("Data loaded : $text")
            }
            catch (e: Exception) {
                println("Timeout exceeded")
            }
        }
    }

    @Test
    fun passDispatcher() {
        runBlocking {
            launch(Dispatchers.Default) {
                print(Thread.currentThread().name)
            }
        }
    }

    @Test
    fun fixedThreadPoolToDispatcher () {
        newFixedThreadPoolContext(5, "workThread").use {
            dispatcher -> runBlocking {
                for( i in 1..3) {
                    launch(dispatcher){
                        println(Thread.currentThread().name)
                        delay(100)
                    }
                }


            //workThread-1 @coroutine#2
            //workThread-2 @coroutine#3
            //workThread-3 @coroutine#4
            }
        }
    }

    @Test
    fun coroutineExceptionHandler() {
        runBlocking {
            suspend fun main() {
                val handler = CoroutineExceptionHandler{
                    _, exception-> println(exception)
                }

                GlobalScope.launch (handler) {

                  launch {
                    throw Exception("Error task A")
                    println("Task A completed")
                  }

                  launch {
                    delay(1000)
                    print("Task B completed")
                  }

                }.join()
                println("Root")
            }

            main()

            //java.lang.Exception: Error task A
            //Root
        }
    }


    @Test
    fun coroutineAwaitHandling() {
        runBlocking {
            val deferredA = async {
                throw Exception("Error in task A")
                println("Task A completed")
            }
            val deferredB = async {
                println("Task B completed")
            }

            try {
                deferredA.await()
                deferredB.await()
            } catch (e: Exception) {
                println("Caught $e")
            }

            print("Root")
        }
        // Caught java.lang.Exception: Error in task A
        // Root
        // java.lang.Exception: Error in task A
    }

    @Test
    fun coroutineAwaitHandlingWithSupervisor() {
        runBlocking {
            supervisorScope {
                val deferredA = async {
                    throw Exception("Error in task A")
                    println("Task A completed")
                }
                val deferredB = async {
                    println("Task B completed")
                }

                try {
                    deferredA.await()
                    deferredB.await()
                } catch (e: Exception) {
                    println("Caught $e")
                }

                print("Root")
            }
        }
        // Task B completed
        // Caught java.lang.Exception: Error in task A
        // Root
    }
}
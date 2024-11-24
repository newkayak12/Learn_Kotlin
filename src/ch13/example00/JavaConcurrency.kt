package ch13.example00

import org.testng.annotations.Test
import kotlin.concurrent.thread
import kotlin.concurrent.timer

class JavaConcurrency {

    @Test
    fun threadTest() {
        println("Starting a thread...")

        thread(name = "Worker", isDaemon = true) {
            for ( i in 1..5) {
                println("${Thread.currentThread().name} : $i" )
                Thread.sleep(150)
            }
        }

        Thread.sleep(500)
        println("Shutting down...")

    //Starting a thread...
        //Worker : 1
        //Worker : 2
        //Worker : 3
        //Worker : 4
        //Shutting down...
    }

    @Test
    fun timerTest() {
        println("Starting a thread...")
        var counter= 0

        timer(period = 150, name = "Worker", daemon = true) {
            println("${Thread.currentThread().name} ${++ counter}")
        }

        Thread.sleep(500)
        println("Shutting down...")

        //Starting a thread...
        //Worker 1
        //Worker 2
        //Worker 3
        //Worker 4
        //Shutting down...
    }

    @Test
    fun synchronizedTest() {
        var count = 0
        val lock = Any()

        for( i in 1..5 ) {
            thread(isDaemon = false) {
                synchronized(lock) {
                    count += i
                    println(count)
                }
            }
        }
        //1
        //3
        //6
        //11
        //15
    }

    class Counter {
        private var value = 0
        @Synchronized fun addAndPrint(value: Int) {
            this.value += value
            println(value)
        }
    }

    @Test
    fun annotationSynchronized () {
        val counter = Counter()
        for (i in 1..5) {
            thread(isDaemon = false) { counter.addAndPrint(i) }
        }
        //1
        //2
        //3
        //4
        //5
    }
}
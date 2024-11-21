package ch13.example00

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.testng.annotations.Test
import kotlin.random.Random

class CoroutineConcurrentChannel {
    @Test
    fun backPressure () {
        runBlocking {
            val streamSize = 5
            val channel = Channel<Int>(3)

            launch {
                for ( n in 1..streamSize) {
                    delay(Random.nextLong(100))
                    val square = n*n
                    println("Sending $square")
                    channel.send(square)
                }
            }

            launch {
                for ( n in 1..streamSize) {
                    delay(Random.nextLong(100))
                    val receiving = channel.receive()
                    println("Receiving: $receiving")
                }
            }
        }
    }

    @Test
    fun ProducerScopeTest() {
        runBlocking {
            val channel = produce {
                for( n in 1..5) {
                    val square = n*n
                    println("Sending $square")
                    send(square)
                }
            }



            launch {
                channel.consumeEach { println("Receiving: $it") }
            }
        }

        // Sending 1
        // Receiving: 1
        // Sending 4
        // Sending 9
        // Receiving: 4
        // Receiving: 9
        // Sending 16
        // Sending 25
        // Receiving: 16
        // Receiving: 25

    }
}
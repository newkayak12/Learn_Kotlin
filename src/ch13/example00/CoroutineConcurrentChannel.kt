package ch13.example00

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
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

    @Test
    fun tickerTest (){
        runBlocking {
            val ticker = ticker(100)
            println(withTimeoutOrNull(50){ ticker.receive() })
            println(withTimeoutOrNull(60){ ticker.receive() })
            delay(250)
            println(withTimeoutOrNull(1){ ticker.receive() })
            println(withTimeoutOrNull(60){ ticker.receive() })
            println(withTimeoutOrNull(60){ ticker.receive() })
        }
        //null
        //kotlin.Unit
        //kotlin.Unit
        //kotlin.Unit
        //null
    }


    sealed class AccountMessage
    class GetBalance (val amount: CompletableDeferred<Long>): AccountMessage()
    class Deposit(val amount: Long): AccountMessage()
    class Withdraw(
        val amount: Long,
        val isPermitted: CompletableDeferred<Boolean>
    ): AccountMessage()

    fun CoroutineScope.accountManager (
        initialBalance: Long
    ) = actor<AccountMessage> {
        var balance = initialBalance

        for (message in channel) {
            when (message) {
                is GetBalance -> message.amount.complete(balance)

                is Deposit -> {
                    balance += message.amount
                    println("Deposited ${message.amount}")
                }

                is Withdraw -> {
                    val canWithdraw = balance >= message.amount
                    if( canWithdraw ) {
                        balance -= message.amount
                        println("Withdrawn ${message.amount}")
                    }
                    message.isPermitted.complete(canWithdraw)
                }
            }
        }
    }

    private suspend fun SendChannel<AccountMessage>.deposit(
        name: String,
        amount: Long
    ) {
        send(Deposit(amount))
        println("$name: deposit $amount")
    }
    private suspend fun SendChannel<AccountMessage>.tryWithdraw(
        name: String,
        amount: Long
    ){
        val status = CompletableDeferred<Boolean>().let {
            send(Withdraw(amount, it))
            if (it.await()) "OK" else "DENIED"
        }
        println("$name: withdraw $amount ($status)")
    }

    private suspend fun SendChannel<AccountMessage>.printBalance(
        name: String
    ) {
        val balance =  CompletableDeferred<Long>().let {
            send(GetBalance(it))
            it.await()
        }
        println("$name: balance is $balance")
    }

    @Test
    fun actorTest() {
        runBlocking {
            val manager = accountManager(100)
            withContext(Dispatchers.Default) {
                launch {
                    manager.deposit("Client #1", 50)
                    manager.printBalance("Client #1")
                }

                launch {
                    manager.tryWithdraw("Client #2", 100)
                    manager.printBalance("Client #2")
                }

                manager.tryWithdraw("Client #0", 1000)
                manager.printBalance("Client #0")
                manager.close()
            }
        }
        //Client #1: deposit 50
        //Deposited 50
        //Withdrawn 100
        //Client #0: withdraw 1000 (DENIED)
        //Client #1: balance is 50
        //Client #2: withdraw 100 (OK)
        //Client #2: balance is 50
        //Client #0: balance is 50
    }
}
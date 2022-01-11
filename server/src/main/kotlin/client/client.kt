package client

import cs236351.transactionManager.*
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.io.Closeable
import java.util.concurrent.TimeUnit

class HelloWorldClient(private val channel: ManagedChannel) : Closeable {
    private val stub: TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineStub =
        TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineStub(channel)

    suspend fun submitTransaction() {
        val request = transaction {
            txId = "0";
            inputs.addAll(listOf(
                uTxO {
                    txId = "1";
                    address = "1";
                },
            ))
            outputs.addAll(listOf(
                transfer {
                    srcAddress = "1";
                    dstAddress = "2"
                    coins = 5;
                },
            ))
        }

        val response = stub.submitTransaction(request)
        println("Received: ${response.type}, ${response.message}")
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

/**
 * Greeter, uses first argument as name to greet if present;
 * greets "world" otherwise.
 */
suspend fun main(args: Array<String>) {
    val port = 9190

    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()

    val client = HelloWorldClient(channel)

    client.submitTransaction()
}
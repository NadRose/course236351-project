package main

import cs236351.transactionManager.TransactionManagerServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootBoilerplateApplication

val shards = (1..System.getenv("SHARDS_NUM")!!.toInt()).map { "SHARD_$it" }
const val retry = 3
const val timeout = 4000
val servers = (1..System.getenv("SERVERS_NUM")!!.toInt()).map { "manager$it.zk.local" }
val channels = servers.associateWith {
    ManagedChannelBuilder.forAddress(it, 9091 + System.getenv("SERVER_ID")!!.toInt()).usePlaintext().build()!!
}
val stubMap = servers.associateWith {
    TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineStub(channels[it]!!)
}
val serverAddress = "manager${System.getenv("SERVER_ID")!!}.zk.local"
val membershipName = System.getenv("MEMBERSHIP")!!

suspend fun main(args: Array<String>) {

    // -------------------------------------------- http setup --------------------------------------------
    System.setProperty("server.port", System.getenv("HTTP_PORT"))
    runApplication<SpringBootBoilerplateApplication>(*args)

    initPath()
}

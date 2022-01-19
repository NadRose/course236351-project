package main

import cs236351.transactionManager.TransactionManagerServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val shards = (1..System.getenv("SHARDS_NUM")!!.toInt()).map { "SHARD_$it" }
const val retry = 3
const val timeout = 4000

//val servers = (1..System.getenv("SERVERS_NUM")!!.toInt()).map { "manager$it.zk.local" }
//val channels = servers.associateWith {
//    ManagedChannelBuilder.forAddress(it, 9091 + System.getenv("SERVER_ID")!!.toInt()).usePlaintext().build()!!
//}
//val stubMap = servers.associateWith {
//    TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineStub(channels[it]!!)
//}
//val serverAddress = "manager${System.getenv("SERVER_ID")!!}.zk.local"
//val membershipName = System.getenv("MEMBERSHIP")!!

val servers = (1..System.getenv("SERVERS_NUM")!!.toInt()).map { "manager$it.zk.local" }
val channels = servers.mapIndexed { index, s ->
    s to ManagedChannelBuilder.forAddress("localhost", 9191 + index).usePlaintext().build()!!
}.toMap()

val stubMap = servers.associateWith {
    TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineStub(channels[it]!!)
}
val serverAddress = "manager${System.getenv("SERVER_ID")!!}.zk.local"
val membershipName = System.getenv("MEMBERSHIP")!!

fun extractServerId (name: String): Int {
    return name.filter { it.isDigit() }.toInt()
}

@SpringBootApplication
class SpringBootBoilerplateApplication

suspend fun main(args: Array<String>) {

    println(channels)
    // -------------------------------------------- http setup --------------------------------------------
    println(System.getenv("HTTP_PORT"))
    System.setProperty("server.port", System.getenv("HTTP_PORT"))
    runApplication<SpringBootBoilerplateApplication>(*args)

    initPath()
}

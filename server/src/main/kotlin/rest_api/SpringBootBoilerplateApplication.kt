package rest_api

import cs236351.transactionManager.TransactionManagerServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import main.initPath
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val shards = (1..System.getenv("SHARDS_NUM")!!.toInt()).map { "SHARD_$it" }
const val retry = 3
const val timeout: Long = 4

val servers = (1..System.getenv("SERVERS_NUM")!!.toInt()).map { "manager$it.zk.local" }
val channels = servers.associateWith {
    ManagedChannelBuilder.forAddress(it, 9191).usePlaintext().build()!!
}
val stubMap = servers.associateWith {
    TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineStub(channels[it]!!)
}
val serverAddress = "manager${System.getenv("SERVER_ID")!!}.zk.local"
val membershipName = System.getenv("MEMBERSHIP")!!

//val servers = (1..System.getenv("SERVERS_NUM")!!.toInt()).map { "manager$it.zk.local" }
//val channels = servers.mapIndexed { index, s ->
//    s to ManagedChannelBuilder.forAddress("localhost", 9191 + index)
//        .usePlaintext()
//        .build()!!
//}.toMap()
//
//val stubMap = servers.associateWith {
//    TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineStub(channels[it]!!)
//}
//val serverAddress = "manager${System.getenv("SERVER_ID")!!}.zk.local"
//val membershipName = System.getenv("MEMBERSHIP")!!

@SpringBootApplication
class SpringBootBoilerplateApplication

suspend fun main(args: Array<String>) {

    // -------------------------------------------- http setup --------------------------------------------
    System.setProperty("server.port", System.getenv("HTTP_PORT"))
    runApplication<SpringBootBoilerplateApplication>(*args)

    initPath()
}

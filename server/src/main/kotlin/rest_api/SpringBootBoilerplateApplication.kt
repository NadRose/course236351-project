package rest_api

import io.grpc.ServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.log4j.BasicConfigurator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import rpc.TransactionManagerRPCService
import zookeeper.kotlin.examples.Membership
import zookeeper.kotlin.examples.makeConnectionString
import zookeeper.kotlin.examples.withZooKeeper
import zookeeper.kotlin.zookeeper.ZChildren
import zookeeper.kotlin.zookeeper.ZooKeeperKt

@SpringBootApplication
class SpringBootBoilerplateApplication

suspend fun main(args: Array<String>) {
    BasicConfigurator.configure()

    // -------------------------------------------- http setup --------------------------------------------
    System.setProperty("server.port", args[0])
    runApplication<SpringBootBoilerplateApplication>(*args)

    // -------------------------------------------- gRPC setup --------------------------------------------
    val transactionManagerRPCService = TransactionManagerRPCService()

    // set gRPC server port and start server
    val server = ServerBuilder.forPort(args[1].toInt())
        .apply {
            addService(transactionManagerRPCService)
        }
        .build()

    withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
        server.start()
    }

    // -------------------------------------------- zookeeper setup --------------------------------------------
    val zkSockets =  Pair("127.0.0.1", 2181)
    val zkConnectionString = makeConnectionString(listOf(zkSockets))

    withZooKeeper(zkConnectionString) {
        createZookeeperMembership(args[2], args[1], it)
    }
}

fun createZookeeperMembership(membershipName: String, id: String, zk: ZooKeeperKt) = runBlocking {
    val mem = Membership.make(zk, membershipName)

    val chan = Channel<ZChildren>()
    mem.onChange = {
        chan.send(mem.queryMembers())
    }
    println("were here")

    val task = launch {
        for (members in chan) {
            println("Members: ${members.joinToString(", ")}")
        }
    }

    chan.send(mem.queryMembers())
    mem.join(id)
    task.join()
}

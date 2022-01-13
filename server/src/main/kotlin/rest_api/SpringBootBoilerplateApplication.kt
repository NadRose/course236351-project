package rest_api

import io.grpc.ServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.log4j.BasicConfigurator
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooKeeper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import rpc.TransactionManagerRPCService
import zookeeper.kotlin.examples.Membership
import zookeeper.kotlin.examples.makeConnectionString
import zookeeper.kotlin.zookeeper.ZookeeperKtClient

@SpringBootApplication
class SpringBootBoilerplateApplication

suspend fun main(args: Array<String>) {

    // -------------------------------------------- zookeeper setup --------------------------------------------
    BasicConfigurator.configure()
    val zkSockets = Pair("127.0.0.1", 2181)
    val zkConnectionString = makeConnectionString(listOf(zkSockets))

    println("--- Connecting to ZooKeeper @ $zkConnectionString")
    val chan = Channel<Unit>()
    val zk = ZooKeeper(zkConnectionString, 1000) { event ->
        if (event.state == Watcher.Event.KeeperState.SyncConnected &&
            event.type == Watcher.Event.EventType.None
        ) {
            runBlocking { chan.send(Unit) }
        }
    }
    chan.receive()
    println("--- Connected to ZooKeeper")
    val zkClient = ZookeeperKtClient(zk)

    val membership = Membership.make(zkClient, System.getenv("MEMBERSHIP"))

    membership.join(System.getenv("RPC_PORT"))

    // -------------------------------------------- gRPC setup --------------------------------------------
    val transactionManagerRPCService = TransactionManagerRPCService(zkClient, membership)

    // set gRPC server port and start server
    val server = ServerBuilder.forPort(System.getenv("RPC_PORT").toInt())
        .apply {
            addService(transactionManagerRPCService)
        }
        .build()

    withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
        server.start()
    }

    // -------------------------------------------- http setup --------------------------------------------
    System.setProperty("server.port", System.getenv("HTTP_PORT"))
    runApplication<SpringBootBoilerplateApplication>(*args)

    // -------------------------------------------- shutdown gracefully --------------------------------------------
    withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
        server.awaitTermination()
        zk.close()
    }
}

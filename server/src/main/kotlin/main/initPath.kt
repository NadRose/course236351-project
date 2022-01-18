package main

import com.google.protobuf.ByteString
import cs236351.transactionManager.consensusMessage
import cs236351.transactionManager.uTxO
import io.grpc.ServerBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import multipaxos.*
import org.apache.log4j.BasicConfigurator
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooKeeper
import rest_api.repository.model.*
import rpc.*
import zookeeper.kotlin.createflags.Ephemeral
import zookeeper.kotlin.createflags.Persistent
import zookeeper.kotlin.examples.makeConnectionString
import zookeeper.kotlin.zookeeper.ZookeeperKtClient

val TransactionBiSerializer = object : ByteStringBiSerializer<List<TimedTransactionGRPC>> {
    override fun serialize(obj: List<TimedTransactionGRPC>): ByteString {
        return toByteString(obj)
    }

    override fun deserialize(serialization: ByteString): List<TimedTransactionGRPC> {
        return fromByteString(serialization)
    }
}

suspend fun initPath() = coroutineScope {

    // -------------------------------------------- zookeeper setup --------------------------------------------
    BasicConfigurator.configure()
    val zkSockets = Pair("zoo1.zk.local", 2181)
    val zkConnectionString = makeConnectionString(listOf(zkSockets))

    println("--- Connecting to ZooKeeper @ $zkConnectionString")
    val chan = Channel<Unit>()
    val zk = ZooKeeper(zkConnectionString, 5000) { event ->
        if (event.state == Watcher.Event.KeeperState.SyncConnected &&
            event.type == Watcher.Event.EventType.None
        ) {
            runBlocking { chan.send(Unit) }
        }
    }
    chan.receive()
    println("--- Connected to ZooKeeper")
    val zkClient = ZookeeperKtClient(zk)


    zkClient.create("/$membershipName") {
        flags = Persistent
        this.ignore(KeeperException.Code.NODEEXISTS)
    }

    zkClient.create("/$membershipName/$serverAddress") {
        flags = Ephemeral
    }

    // Take the ID as the port number
    val id = serverAddress.toInt()

    // Init services
    val learnerService = LearnerService(this)
    val acceptorService = AcceptorService(id)

    // Use the atomic broadcast adapter to use the learner service as an atomic broadcast service
    val atomicBroadcast =
        object : AtomicBroadcast<List<TimedTransactionGRPC>>(learnerService, TransactionBiSerializer) {
            // These are dummy implementations
            override suspend fun _send(byteString: ByteString) {
                val address = zkClient.getChildren("/${shards.last()}") {}.first.last()
                stubMap[address]!!.consensusAddProposal(
                    consensusMessage {
                        message = byteString
                    })
            }

            override fun _deliver(byteString: ByteString) = listOf(biSerializer(byteString))
        }

    /*
     * Don't forget to add the list of learners to the learner service.
     * The learner service is a reliable broadcast service and needs to
     * have a connection with all processes that participate as learners
     */
    learnerService.learnerChannels = channels.values.toList()

    /*
     * You Should implement an omega failure detector.
     */
    val omega = object : OmegaFailureDetector<ID> {
        override var leader: ID = id
        override fun addWatcher(observer: suspend () -> Unit) {
            val leaderRootPath = "/${shards.last()}"
            launch {
                println("old leader is $leader")
                val zNodeList = zkClient.getChildren(leaderRootPath) {
                    watchers += { _, _, _ -> println("watcher activated on $leaderRootPath"); addWatcher(observer) }
                }.first
                leader = if (zNodeList.isNotEmpty()) {
                    zNodeList.last().toInt()
                } else {
                    id
                }
                println("new leader is $leader")
                observer()
            }
        }
    }

// Create a proposer, not that the proposers' id's id and
// the acceptors' id's must be all unique (they break symmetry)
    val proposer = Proposer(
        id = id,
        omegaFD = omega,
        scope = this,
        acceptors = channels.mapKeys { it.key.toInt() }.filterKeys { it != id },
        thisLearner = learnerService,
    )

// Starts The proposer
    proposer.start()

// Build gRPC server
    val transactionManagerRPCService = TransactionManagerRPCService(zkClient, proposer, atomicBroadcast)

    val server = ServerBuilder.forPort(id)
        .apply {
            addService(transactionManagerRPCService)
        }
        .apply {
            addService(acceptorService)
        }
        .apply {
            addService(learnerService)
        }
        .build()

    startReceivingPaxosMessages(atomicBroadcast)

    withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
        server.start()
        server.awaitTermination()
        zk.close()
    }
}

private fun CoroutineScope.startReceivingPaxosMessages(atomicBroadcast: AtomicBroadcast<List<TimedTransactionGRPC>>) {
    launch {
        for ((`seq#`, msg) in atomicBroadcast.stream) {
            println("Message #$`seq#`  received at server $serverAddress")
            if (msg.isNotEmpty() && msg[0].timestamp == 0.toLong()) {
                sendOwnLedger(msg[0].txId)
                continue
            }
            msg.forEach { timedTransaction ->
                val fromAddress = timedTransaction.inputs[0].address
                if (findOwnerShard(fromAddress) == membershipName) {
                    transactionsMap.getOrPut(fromAddress) {
                        sortedSetOf()
                    }.add(timedTransaction)
                }

                timedTransaction.outputs.forEach { transfer ->
                    val address = transfer.dstAddress
                    val utxo = uTxO {
                        txId = timedTransaction.txId
                        this.address = address
                    }
                    if (findOwnerShard(address) == membershipName) {
                        transactionsMap.getOrPut(address) {
                            sortedSetOf()
                        }.add(timedTransaction)

                        if (!missingUtxoPool.getOrElse(address) { mutableListOf() }.remove(utxo)) {
                            utxoPool.getOrPut(address) {
                                mutableListOf()
                            }.add(utxo)
                        }
                    }
                }
            }
        }
    }
}

fun sendOwnLedger(address: String) {
    val ownLedger: MutableList<TimedTransactionGRPC> = mutableListOf(
        TimedTransactionGRPC(
            membershipName,
            mutableListOf(),
            mutableListOf(),
            timestamp = 0,
        )
    )
    transactionsMap.values.forEach {
        ownLedger.addAll(it)
    }

    val owner = stubMap[address]!!
    runBlocking {
        owner.consensusPostLedger(toProto(ownLedger))
    }
}

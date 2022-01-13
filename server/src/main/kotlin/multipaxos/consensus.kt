package multipaxos

import com.google.protobuf.ByteString
import rest_api.repository.model.Transaction
import com.google.protobuf.kotlin.toByteStringUtf8
import io.grpc.ManagedChannelBuilder
import io.grpc.ServerBuilder
import kotlinx.coroutines.*

val TransactionBiSerializer = object : ByteStringBiSerializer<Transaction> {
    override fun serialize(obj: Transaction): ByteString {
        return ByteString.copyFrom(obj.toString().toByteArray())
    }
    override fun deserialize(serialization: ByteString): Transaction {
        return Transaction("1", inputs = mutableListOf(), outputs = mutableListOf())
    }
}
//
//class Consensus(learner: LearnerService, serializer: TransactionsSerializer) :
//    AtomicBroadcast<Transaction>(learner, serializer) {
//    private var seq = 0
//
//    override suspend fun _send(byteString: ByteString) {
//        this.learner.observers[0].invoke(seq++, byteString)
//    }
//
//    override fun _deliver(byteString: ByteString): List<Transaction> {
//        return listOf(biSerializer.deserialize(byteString))
//    }
//
//}

suspend fun gRPCAndConsensusRunner(args: Array<String>) = coroutineScope {

    // Displays all debug messages from gRPC
    org.apache.log4j.BasicConfigurator.configure()

    // Take the ID as the port number
    val id = args[0].toInt()

    // Init services
    val learnerService = LearnerService(this)
    val acceptorService = AcceptorService(id)

    // Build gRPC server
    val server = ServerBuilder.forPort(id)
        .apply {
            if (id > 0) // Apply your own logic: who should be an acceptor
                addService(acceptorService)
        }
        .apply {
            if (id > 0) // Apply your own logic: who should be a learner
                addService(learnerService)
        }
        .build()

    // Use the atomic broadcast adapter to use the learner service as an atomic broadcast service
    val atomicBroadcast = object : AtomicBroadcast<Transaction>(learnerService, TransactionBiSerializer) {
        // These are dummy implementations
        override suspend fun _send(byteString: ByteString) = throw NotImplementedError()
        override fun _deliver(byteString: ByteString) = listOf(biSerializer(byteString))
    }

    withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
        server.start()
    }

    // Create channels with clients
    val chans = listOf(8980, 8981, 8982).associateWith {
        ManagedChannelBuilder.forAddress("localhost", it).usePlaintext().build()!!
    }

    /*
     * Don't forget to add the list of learners to the learner service.
     * The learner service is a reliable broadcast service and needs to
     * have a connection with all processes that participate as learners
     */
    learnerService.learnerChannels = chans.filterKeys { it != id }.values.toList()

    /*
     * You Should implement an omega failure detector.
     */
    val omega = object : OmegaFailureDetector<ID> {
        override val leader: ID get() = id
        override fun addWatcher(observer: suspend () -> Unit) {}
    }

    // Create a proposer, not that the proposers id's id and
    // the acceptors id's must be all unique (they break symmetry)
    val proposer = Proposer(
        id = id, omegaFD = omega, scope = this, acceptors = chans,
        thisLearner = learnerService,
    )

    // Starts The proposer
    proposer.start()

    startReceivingMessages(atomicBroadcast)

    // "Key press" barrier so only one proposer sends messages
    withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
        System.`in`.read()
    }
    startGeneratingMessages(id, proposer)
    withContext(Dispatchers.IO) { // Operations that block the current thread should be in a IO context
        server.awaitTermination()
    }
}

private fun CoroutineScope.startGeneratingMessages(
    id: Int,
    proposer: Proposer,
) {
    launch {
        println("Started Generating Messages")
        (1..100).forEach {
            delay(1000)
            val prop = "[Value no $it from $id]".toByteStringUtf8()
                .also { println("Adding Proposal ${it.toStringUtf8()!!}") }
            proposer.addProposal(prop)
        }
    }
}

private fun CoroutineScope.startReceivingMessages(atomicBroadcast: AtomicBroadcast<Transaction>) {
    launch {
        for ((`seq#`, msg) in atomicBroadcast.stream) {
            println("Message #$`seq#`: $msg  received!")
            // insert transaction to ledger
        }
    }
}
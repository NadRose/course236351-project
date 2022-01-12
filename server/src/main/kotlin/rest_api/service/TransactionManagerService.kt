package rest_api.service

import cs236351.transactionManager.*
import rest_api.repository.model.Transaction as ModelTransaction
import rest_api.repository.model.Transfer as ModelTransfer
import rest_api.repository.model.UTxO as ModelUTxO
import rest_api.repository.model.TimedTransaction as ModelTimedTransaction
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import rest_api.repository.model.fromProto
import rest_api.repository.model.toProto


@Service
class TransactionManagerService {
    private val servers = listOf(9190, 9191)

    private val channels = servers.associateWith {
        ManagedChannelBuilder.forAddress("localhost", it).usePlaintext().build()!!
    }

    //    private val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build()
    private val stubMap = servers.associateWith {
        TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineStub(channels[it]!!)
    }

    private fun findOwner(address: String): Int {
        // TODO: implement after zookeeper integration
        return 9190
    }

    fun submitTransaction(transaction: ModelTransaction, address: String): String = runBlocking {
        val owner = stubMap[findOwner(address)]!!
        return@runBlocking owner.submitTransaction(toProto(transaction)).toString()
    }

    fun makeTransfer(transfer: ModelTransfer, address: String): String = runBlocking {
        val owner = stubMap[findOwner(address)]!!
        return@runBlocking owner.makeTransfer(toProto(transfer, address)).toString()
    }

//    fun submitAtomicTxList(transactionList: List<Transaction>, address: String): String {
//        // We assume clients are honest and therefore support "zero transactions list" (all tx-id's are "0")
//        // under the assumption that all input utxo are valid and unrelated - meaning can be submitted atomically
//        // or "Non-zero transaction list (all tx-id's are valid uuid)
//        if (isValidTxList(transactionList)) {
//            transactionList.forEach {
//                val address = it.inputs[0].address
//                // TODO: send gRPC to address corresponding shard
//            }
//            return "Success submitting Tx list"
//        }
//        return "Failed to submit Tx list"
//    }

    fun getUTxOs(address: String): List<ModelUTxO> = runBlocking {
        val request = addressRequest {
            this.address = address
        }
        val owner = stubMap[findOwner(address)]!!
        return@runBlocking owner.getUTxOs(request).utxoListList.map {
            fromProto(it)
        }
    }

    fun getTxHistory(address: String, limit: Int?): List<ModelTimedTransaction> = runBlocking {
        val request = addressRequest {
            this.address = address
            this.limit = limit ?: Int.MAX_VALUE
        }
        val owner = stubMap[findOwner(address)]!!
        return@runBlocking owner.getTxHistory(request).transactionListList.map { fromProto(it) }
    }

//    fun getLedgerHistory(limit: Int?): List<Transaction> {
//        // TODO: Read ledger from consensus mechanism or some shit.
//        return listOf()
//    }
}


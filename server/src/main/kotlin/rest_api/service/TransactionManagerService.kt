package rest_api.service

import cs236351.transactionManager.*
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.*
import rest_api.repository.model.Transaction as ModelTransaction
import rest_api.repository.model.Transfer as ModelTransfer
import rest_api.repository.model.UTxO as ModelUTxO
import rest_api.repository.model.TimedTransaction as ModelTimedTransaction
import org.springframework.stereotype.Service
import rest_api.repository.model.fromProto
import rest_api.repository.model.toProto
import rest_api.*
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class TransactionManagerService {

    private fun findOwner(address: String): String = runBlocking { //TODO should we retry here as well?
        val owner = stubMap[serverAddress]!!
        return@runBlocking owner.findOwner(addressRequest { this.address = address }).address
    }

    fun submitTransaction(transaction: ModelTransaction, address: String): String {
        val inputTxId = if (transaction.txId == "0") UUID.randomUUID().toString() else transaction.txId
        val transactionReq = ModelTransaction(inputTxId, transaction.inputs, transaction.outputs)
        var retVal = ""

        for (i in 1..retry) runBlocking {
            try {
                val owner = stubMap[findOwner(address)]!!
                retVal = owner.withDeadlineAfter(timeout, TimeUnit.SECONDS)
                    .submitTransaction(toProto(transactionReq)).toString()
            } catch (e: Exception) {
                if (i == retry) {
                    retVal = "Operation failed with $e,\nPlease try again later."
                } else {
                    print("trying to connect to a different server...attempt #$i.\n")
//                    Thread.sleep(10000)
                }
            }
        }
       return retVal
    }

    fun makeTransfer(transfer: ModelTransfer, address: String): String = runBlocking {
        val inputTxId = UUID.randomUUID().toString()
        val owner = stubMap[findOwner(address)]!!
        return@runBlocking owner.makeTransfer(toProto(transfer, address, inputTxId)).toString()
//        val inputTxId = UUID.randomUUID().toString()
//        try {
//            for (i in 1..retry) {
//                runBlocking {
//                    val owner = stubMap[findOwner(address)]!!
//                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
//                        .makeTransfer(toProto(transfer, address, inputTxId)).toString()
//                }
//            }
//            return "Operation failed. Please try again later."
//        } catch (e: Error) {
//            return "Operation failed. Please try again later."
//        }
    }

    fun submitAtomicTxList(transactionList: List<ModelTransaction>): String = runBlocking {
        val owner = stubMap[serverAddress]!!
        return@runBlocking owner.submitAtomicTransaction(toProto(transactionList)).toString()
//        try {
//            for (i in 1..retry) {
//                runBlocking {
//                    val owner = stubMap[serverAddress]!!
//                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
//                        .submitAtomicTransaction(toProto(transactionList)).toString()
//                }
//            }
//            return "Operation failed. Please try again later."
//        } catch (e: Error) {
//            return "Operation failed. Please try again later."
//        }
    }

    fun getUTxOs(address: String): List<ModelUTxO> = runBlocking {
        val request = addressRequest {
            this.address = address
        }
        val owner = stubMap[findOwner(address)]!!
        return@runBlocking owner.getUTxOs(request).utxoListList.map { fromProto(it) }
//        try {
//            for (i in 1..retry) {
//                println("finding utxo for address $address")
//                runBlocking {
//                    val request = addressRequest {
//                        this.address = address
//                    }
//                    val owner = stubMap[findOwner(address)]!!
//                    return@runBlocking owner.getUTxOs(request).utxoListList.map { fromProto(it) }
//                }
//            }
//            return listOf()
//        } catch (e: Error) {
//            return listOf()
//        }
    }

    fun getTxHistory(address: String, limit: Int?): List<ModelTimedTransaction> = runBlocking {
        val request = addressRequest {
            this.address = address
            this.limit = limit ?: Int.MAX_VALUE
        }
        val owner = stubMap[findOwner(address)]!!
        return@runBlocking owner.getTxHistory(request).transactionListList.map { fromProto(it) }
//        try {
//            for (i in 1..retry) {
//                runBlocking {
//                    val request = addressRequest {
//                        this.address = address
//                        this.limit = limit ?: Int.MAX_VALUE
//                    }
//                    val owner = stubMap[findOwner(address)]!!
//                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
//                        .getTxHistory(request).transactionListList.map { fromProto(it) }
//                }
//            }
//            return listOf()
//        } catch (e: Error) {
//            return listOf()
//        }
    }

    fun getLedgerHistory(limit: Int?): List<ModelTimedTransaction> = runBlocking {
        val request = addressRequest {
            address = serverAddress
            this.limit = limit ?: Int.MAX_VALUE
        }
        val owner = stubMap[serverAddress]!!
        return@runBlocking owner.getLedger(request).transactionListList.map { fromProto(it) }
//        try {
//            for (i in 1..retry) {
//                runBlocking {
//                    val request = addressRequest {
//                        this.limit = limit ?: Int.MAX_VALUE
//                    }
//                    val owner = stubMap[findOwner(serverAddress)]!!
//                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
//                        .getLedger(request).transactionListList.map { fromProto(it) }
//                }
//            }
//            return listOf()
//        } catch (e: Error) {
//            return listOf()
//        }
    }
}


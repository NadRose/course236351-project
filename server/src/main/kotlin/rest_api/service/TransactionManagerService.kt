package rest_api.service

import cs236351.transactionManager.*
import rest_api.repository.model.Transaction as ModelTransaction
import rest_api.repository.model.Transfer as ModelTransfer
import rest_api.repository.model.UTxO as ModelUTxO
import rest_api.repository.model.TimedTransaction as ModelTimedTransaction
import kotlinx.coroutines.runBlocking
import main.retry
import main.serverAddress
import main.stubMap
import main.timeout
import org.springframework.stereotype.Service
import rest_api.repository.model.fromProto
import rest_api.repository.model.toProto
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class TransactionManagerService {

    private fun findOwner(address: String): String = runBlocking {
        val owner = stubMap[serverAddress]!!
        return@runBlocking owner.findOwner(addressRequest { this.address = address }).address
    }

    fun submitTransaction(transaction: ModelTransaction, address: String): String {
        val inputTxId = if (transaction.txId == "0") UUID.randomUUID().toString() else transaction.txId
        val transactionReq = ModelTransaction(inputTxId, transaction.inputs, transaction.outputs)
        try {
            for (i in 1..retry) {
                runBlocking {
                    val owner = stubMap[findOwner(address)]!!
                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
                        .submitTransaction(toProto(transactionReq)).toString()
                }
            }
            return "Operation failed. Please try again later."
        } catch (e: Error) {
            return "Operation failed. Please try again later."
        }
    }

    fun makeTransfer(transfer: ModelTransfer, address: String): String {
        val inputTxId = UUID.randomUUID().toString()
        try {
            for (i in 1..retry) {
                runBlocking {
                    val owner = stubMap[findOwner(address)]!!
                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
                        .makeTransfer(toProto(transfer, address, inputTxId)).toString()
                }
            }
            return "Operation failed. Please try again later."
        } catch (e: Error) {
            return "Operation failed. Please try again later."
        }
    }

    fun submitAtomicTxList(transactionList: List<ModelTransaction>): String {
        try {
            for (i in 1..retry) {
                runBlocking {
                    val owner = stubMap[serverAddress]!!
                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
                        .submitAtomicTransaction(toProto(transactionList)).toString()
                }
            }
            return "Operation failed. Please try again later."
        } catch (e: Error) {
            return "Operation failed. Please try again later."
        }
    }

    fun getUTxOs(address: String): List<ModelUTxO> {
        try {
            for (i in 1..retry) {
                runBlocking {
                    val request = addressRequest {
                        this.address = address
                    }
                    val owner = stubMap[findOwner(address)]!!
                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
                        .getUTxOs(request).utxoListList.map { fromProto(it) }
                }
            }
            return listOf()
        } catch (e: Error) {
            return listOf()
        }
    }

    fun getTxHistory(address: String, limit: Int?): List<ModelTimedTransaction> {
        try {
            for (i in 1..retry) {
                runBlocking {
                    val request = addressRequest {
                        this.address = address
                        this.limit = limit ?: Int.MAX_VALUE
                    }
                    val owner = stubMap[findOwner(address)]!!
                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
                        .getTxHistory(request).transactionListList.map { fromProto(it) }
                }
            }
            return listOf()
        } catch (e: Error) {
            return listOf()
        }
    }

    fun getLedgerHistory(limit: Int?): List<ModelTimedTransaction> {
        try {
            for (i in 1..retry) {
                runBlocking {
                    val request = addressRequest {
                        this.limit = limit ?: Int.MAX_VALUE
                    }
                    val owner = stubMap[findOwner(serverAddress)]!!
                    return@runBlocking owner.withDeadlineAfter(timeout.toLong(), TimeUnit.MILLISECONDS)
                        .getLedger(request).transactionListList.map { fromProto(it) }
                }
            }
            return listOf()
        } catch (e: Error) {
            return listOf()
        }
    }
}


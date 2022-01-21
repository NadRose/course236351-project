package rest_api.service

import cs236351.transactionManager.*
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

    private fun findOwner(address: String): String = runBlocking {
        val owner = stubMap[serverAddress]!!
        return@runBlocking owner.findOwner(addressRequest { this.address = address }).address
    }

    fun submitTransaction(transaction: ModelTransaction, address: String): String {
        val inputTxId = if (transaction.txId == "0") UUID.randomUUID().toString() else transaction.txId
        val transactionReq = ModelTransaction(inputTxId, transaction.inputs, transaction.outputs)
        var retVal = ""

        for (i in 1..retry) {
            runBlocking {
                try {
                    val owner = stubMap[findOwner(address)]!!
                    retVal = owner.withDeadlineAfter(timeout, TimeUnit.SECONDS)
                        .submitTransaction(toProto(transactionReq)).toString()

                } catch (e: Exception) {
                    if (i == retry) {
                        retVal = "Operation failed with $e,\nPlease try again later."
                    } else {
                        print("trying to connect to a different server...attempt #$i.\n")
                    }
                }
            }
            if (retVal != "") break
        }
        return retVal
    }

    fun makeTransfer(transfer: ModelTransfer, address: String): String {
        val inputTxId = UUID.randomUUID().toString()
        var retVal = ""

        for (i in 1..retry) {
            runBlocking {
                try {
                    val owner = stubMap[findOwner(address)]!!
                    retVal = owner.withDeadlineAfter(timeout, TimeUnit.SECONDS)
                        .makeTransfer(toProto(transfer, address, inputTxId)).toString()
                } catch (e: Exception) {
                    if (i == retry) {
                        retVal = "Operation failed with $e,\nPlease try again later."
                    } else {
                        println("trying to connect to a different server...attempt #$i.")
                    }
                }
            }
            if (retVal != "") break
        }
        return retVal
    }

    fun submitAtomicTxList(transactionList: List<ModelTransaction>): String {
        var retVal = ""
        for (i in 1..retry) {
            runBlocking {
                try {
                    val owner = stubMap[serverAddress]!!
                    retVal =
                        owner.withDeadlineAfter(timeout, TimeUnit.SECONDS)
                            .submitAtomicTransaction(toProto(transactionList))
                            .toString()
                } catch (e: Exception) {
                    if (i == retry) {
                        retVal = "Operation failed with $e,\nPlease try again later."
                    } else {
                        print("trying to connect to a different server...attempt #$i.\n")
                    }
                }
            }
            if (retVal != "") break
        }
        return retVal
    }

    fun getUTxOs(address: String): Any {
        val request = addressRequest {
            this.address = address
        }
        var retVal: Any = ""
        for (i in 1..retry) {
            runBlocking {
                try {
                    val owner = stubMap[findOwner(address)]!!
                    retVal =
                        owner.withDeadlineAfter(timeout, TimeUnit.SECONDS)
                            .getUTxOs(request).utxoListList.map { fromProto(it) }

                } catch (e: Exception) {
                    if (i == retry) {
                        retVal = "Operation failed with $e,\nPlease try again later."
                    } else {
                        print("trying to connect to a different server...attempt #$i.\n")
                    }
                }
            }
            if (retVal != "") break
        }
        return retVal
    }

    fun getTxHistory(address: String, limit: Int?): Any {
        val request = addressRequest {
            this.address = address
            this.limit = limit ?: Int.MAX_VALUE
        }
        var retVal: Any = ""
        for (i in 1..retry) {
            runBlocking {
                try {
                    val owner = stubMap[findOwner(address)]!!
                    retVal =
                        owner.withDeadlineAfter(timeout, TimeUnit.SECONDS)
                            .getTxHistory(request).transactionListList.map { fromProto(it) }

                } catch (e: Exception) {
                    if (i == retry) {
                        retVal = "Operation failed with $e,\nPlease try again later."
                    } else {
                        print("trying to connect to a different server...attempt #$i.\n")
                    }
                }
            }
            if (retVal != "") break
        }
        return retVal

    }

    fun getLedgerHistory(limit: Int?): Any {
        val request = addressRequest {
            address = serverAddress
            this.limit = limit ?: Int.MAX_VALUE
        }
        var retVal: Any = ""
        for (i in 1..retry) {
            runBlocking {
                try {
                    val owner = stubMap[serverAddress]!!
                    retVal =
                        owner.withDeadlineAfter(timeout, TimeUnit.SECONDS)
                            .getLedger(request).transactionListList.map { fromProto(it) }

                } catch (e: Exception) {
                    if (i == retry) {
                        retVal = "Operation failed with $e,\nPlease try again later."
                    } else {
                        print("trying to connect to a different server...attempt #$i.\n")
                    }
                }
            }
            if (retVal != "") break
        }
        return retVal
    }
}


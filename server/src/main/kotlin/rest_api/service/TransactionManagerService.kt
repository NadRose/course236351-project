package rest_api.service

import org.springframework.stereotype.Service
import rest_api.repository.model.TimedTransaction
import rest_api.repository.model.Transaction
import rest_api.repository.model.Transfer
import rest_api.repository.model.UTxO
import java.util.*
import kotlin.collections.HashMap

val transactionsMap: HashMap<String, SortedSet<TimedTransaction>> = hashMapOf(
    Pair(
        "1",
        sortedSetOf(
            TimedTransaction(
                txId = "1",
                inputs = mutableListOf(
                    UTxO(
                        "0",
                        "0"
                    )
                ),
                outputs = mutableListOf(
                    Transfer(
                        "1",
                        20u
                    ),
                    Transfer(
                        "2",
                        12u
                    ),
                    Transfer(
                        "0",
                        ULong.MAX_VALUE - 32u
                    )
                ),
                timestamp = System.currentTimeMillis()
            ),
        ),
    ),
)

@Service
class TransactionManagerService() {
    private var UTxOPool: HashMap<String, MutableList<UTxO>> = hashMapOf(
        Pair("1", mutableListOf(UTxO("1", "1"))),
        Pair("2", mutableListOf(UTxO("2", "2"))),
    )
    private var MissingUTxOPool: HashMap<String, MutableList<UTxO>> = hashMapOf()


    fun submitTransaction(transaction: Transaction, address: String): String {
//        if (!isResponsibleForAddress(address)) {
//            // TODO: send gRPC to address corresponding shard
//        }
        val txId = if (transaction.txId == "0") UUID.randomUUID() else transaction.txId
        for (input in transaction.inputs) {
            if (this.UTxOPool[input.address]?.remove(input) == false) {
                this.MissingUTxOPool.getOrPut(input.address) {
                    mutableListOf() // TODO what happens if leader crashes in here? how will the followers know he crashed and process the request?
                }.add(input)
            }
        }
        val tx = TimedTransaction(txId.toString(), transaction.inputs, transaction.outputs, System.currentTimeMillis())
        transactionsMap.getOrPut(address) {
            sortedSetOf()
        }.add(tx)
        //TODO Submit tx to ledger using paxos/atomic broadcast or some shit.

        return txId.toString()
    }

    fun submitAtomicTxList(transactionList: List<Transaction>, address: String): String {
        // We assume clients are honest and therefore support "zero transactions list" (all tx-id's are "0")
        // under the assumption that all input utxo's are valid and unrelated - meaning can be submitted atomically
        // or "Non-zero transaction list (all tx-id's are valid uuid)
        if (isValidTxList(transactionList)) {
            transactionList.forEach {
                val address = it.inputs[0].address
                // TODO: send gRPC to address corresponding shard
            }
            return "Success submitting Tx list"
        }
        return "Failed to submit Tx list"
    }

    fun makeTransfer(transfer: Transfer, address: String): String {
//        if (!isResponsibleForAddress(address)) {
//            // TODO: send gRPC to address corresponding shard
//        }
        val txId = UUID.randomUUID()
        val outputs = mutableListOf(transfer)
        val inputs: MutableList<UTxO> = mutableListOf()
        var curSum: ULong = 0u

        if (!this.UTxOPool.containsKey(address)) {
            return "Operation failed! Not enough available UTxOs."
        }

        val utxosToRemove: MutableList<UTxO> = mutableListOf()
        for (utxo in this.UTxOPool[address].orEmpty()) {
            if (curSum < transfer.coins) {
                inputs.add(utxo)
                curSum += getAmount(utxo)
                utxosToRemove.add(utxo)
            } else break
        }
        if (curSum > transfer.coins) {
            this.UTxOPool[address]?.add(UTxO(txId.toString(), address))
        }
        this.UTxOPool[address]?.removeAll(utxosToRemove)

        val tx = TimedTransaction(txId.toString(), inputs, outputs, System.currentTimeMillis())
        transactionsMap.getOrPut(address) {
            sortedSetOf()
        }.add(tx)

        //TODO Submit tx to ledger using paxos/atomic broadcast or some shit.
        return txId.toString()
    }


    fun getUTxOs(address: String): List<UTxO> {
        return this.UTxOPool[address].orEmpty()
    }

    fun getTxHistory(address: String, limit: Int?): List<TimedTransaction> {
        val resultList = transactionsMap[address]
        resultList?.let {
            return resultList.take(limit ?: resultList.size)
        }
        return listOf()
    }

    fun getLedgerHistory(limit: Int?): List<Transaction> {
        // TODO: Read ledger from consensus mechanism or some shit.
        return listOf()
    }
}


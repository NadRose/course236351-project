package rest_api.service

import org.springframework.stereotype.Service
import rest_api.repository.model.Transaction
import rest_api.repository.model.Transfer
import rest_api.repository.model.UTxO
import rest_api.repository.model.UTxOPoolItem
import java.util.*

@Service
class TransactionManagerService() {
    private var UTxOPool: HashMap<String, MutableList<UTxO>> = hashMapOf(
        Pair("1", mutableListOf(UTxO("1", "1", 5u))),
        Pair("2", mutableListOf(UTxO("2", "2", 7u)))
    )
    private var MissingUTxOPool: HashMap<String, MutableList<UTxO>> = hashMapOf()


    fun submitTransaction(transaction: Transaction, address: String): String {
        val txId = if (transaction.txId == "0") UUID.randomUUID() else transaction.txId
        for (input in transaction.inputs) {
            if (this.UTxOPool[input.address]?.remove(input) == false) {
                this.MissingUTxOPool.getOrPut(input.address) {
                    mutableListOf() // TODO what happens if leader crashes in here? how will the followers know he crashed and process the request?
                }.add(input)
            }
        }
        val tx = Transaction(txId.toString(), transaction.inputs, transaction.outputs)
        //TODO Submit tx to ledger using paxos/atomic broadcast or some shit.

        return txId.toString()
    }

    fun submitAtomicTxList(transactionList: List<Transaction>, address: String): String {
        return "1"
    }

    fun makeTransfer(transfer: Transfer, address: String): String {
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
                curSum += utxo.amount
                utxosToRemove.add(utxo)
            } else break
        }
        if (curSum > transfer.coins) {
            this.UTxOPool[address]?.add(UTxO(txId.toString(), address, curSum - transfer.coins))
        }
        this.UTxOPool[address]?.removeAll(utxosToRemove)

        val tx = Transaction(txId.toString(), inputs, outputs)
        //TODO Submit tx to ledger using paxos/atomic broadcast or some shit.
        return txId.toString()
    }


    fun getUTxOs(address: String): List<UTxO> {
        return this.UTxOPool[address].orEmpty() //TODO fix weird "amount" field name in return value
    }

    fun getTxHistory(address: String, limit: Optional<Int>): List<Transaction> {
        return listOf(Transaction("124", mutableListOf(), mutableListOf()))
    }

    fun getLedgerHistory(): List<Transaction> {
        return listOf()
    }

}
package rest_api.service

import org.springframework.stereotype.Service
import rest_api.repository.model.Transaction
import rest_api.repository.model.Transfer
import rest_api.repository.model.UTxO
import rest_api.repository.model.UTxOPoolItem
import java.util.*

@Service
class TransactionManagerService(){
    private var UTxOPool: HashMap<String, MutableList<UTxO>> = hashMapOf()
    private var MissingUTxOPool: HashMap<String, MutableList<UTxO>> = hashMapOf()


    fun submitTransaction(transaction: Transaction, address: String): String {
        val txId = UUID.randomUUID()
        for (input in transaction.inputs){
            if (this.UTxOPool[input.address]?.remove(input) == false){
                this.MissingUTxOPool.getOrPut(input.address) {
                    mutableListOf() // TODO what happens if leader crashes in here? how will the followers know he crashed and process the request?
                }.add(input) //TODO check if this weird function works
//                if (this.MissingUTxOPool.containsKey(input.address)){
//                    this.MissingUTxOPool[input.address]?.add(input)
//                }
//                else {
//                    this.MissingUTxOPool[input.address] = mutableListOf(input)
//                }
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
        return address
    }

    fun getUTxOs(address: String): List<UTxO> {
        return listOf( UTxO("124", "1243"))
    }

    fun getTxHistory(address: String, limit: Optional<Int>): List<Transaction> {
        return listOf( Transaction("124", mutableListOf(), mutableListOf()))
    }

    fun getLedgerHistory(): List<Transaction> {
        return listOf()
    }

}
package rest_api.repository.model

import cs236351.transactionManager.TimedTransactionKt
import cs236351.transactionManager.Transfer
import cs236351.transactionManager.UTxO

data class Transfer(
    val address: String,
    val coins: ULong
)

data class UTxO(
    val txId: String,
    val address: String,
)

open class Transaction(
    val txId: String,
    val inputs: MutableList<UTxO>,
    val outputs: MutableList<Transfer>
)

class TimedTransaction(
    val txId: String,
    val inputs: MutableList<UTxO>,
    val outputs: MutableList<Transfer>,
    val timestamp: Long
) : Comparable<TimedTransaction> {

    override operator fun compareTo(other: TimedTransaction): Int {
        return (this.timestamp - other.timestamp).toInt()
    }

    fun printTT() {
        println("txId: $txId")
        println("inputs: ")
        inputs.forEach {
            println("   txId: ${it.txId}")
            println("   address: ${it.address}")
        }
        println("outputs: ")
        outputs.forEach {
            println("   dst address: ${it.dstAddress}")
            println("   coins: ${it.coins}")
        }
        println("time: $timestamp")
    }
}

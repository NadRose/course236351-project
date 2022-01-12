package rest_api.repository.model

import com.google.protobuf.util.Timestamps.toMillis
import cs236351.transactionManager.transaction
import cs236351.transactionManager.transfer
import cs236351.transactionManager.uTxO
import cs236351.transactionManager.Transfer as ProtoTransfer
import cs236351.transactionManager.UTxO as ProtoUTxO
import cs236351.transactionManager.Transaction as ProtoTransaction
import cs236351.transactionManager.TimedTransaction as ProtoTimedTransaction

data class Transfer(
    val address: String,
    val coins: Long
)

data class UTxO(
    val txId: String,
    val address: String,
)

data class Transaction(
    val txId: String,
    val inputs: List<UTxO>,
    val outputs: List<Transfer>
)

data class TimedTransaction(
    val txId: String,
    val inputs: List<UTxO>,
    val outputs: List<Transfer>,
    val timestamp: Long
)

class TimedTransactionGRPC(
    val txId: String,
    val inputs: MutableList<ProtoUTxO>,
    val outputs: MutableList<ProtoTransfer>,
    val timestamp: Long
) : Comparable<TimedTransactionGRPC> {

    override operator fun compareTo(other: TimedTransactionGRPC): Int {
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

fun toProto(transaction: Transaction): ProtoTransaction {
    return transaction {
        txId = transaction.txId;
        inputs.addAll(transaction.inputs.map {
            uTxO {
                txId = it.txId
                address = it.address
            }
        })
        outputs.addAll(transaction.outputs.map {
            transfer {
                srcAddress = transaction.inputs[0].address
                dstAddress = it.address
                coins = it.coins
            }
        })
    }
}

fun toProto(transfer: Transfer, srcAddress: String): ProtoTransfer {
    return transfer {
        this.srcAddress = srcAddress;
        dstAddress = transfer.address
        coins = transfer.coins
    }
}

fun fromProto(utxo: ProtoUTxO): UTxO {
    return UTxO(txId = utxo.txId, address = utxo.address)
}

fun fromProto(transfer: ProtoTransfer): Transfer {
    return Transfer(address = transfer.dstAddress, coins = transfer.coins)
}

fun fromProto(timedTransaction: ProtoTimedTransaction): TimedTransaction {
    return TimedTransaction(
        txId = timedTransaction.transaction.txId,
        inputs = timedTransaction.transaction.inputsList.map { fromProto(it) },
        outputs = timedTransaction.transaction.outputsList.map { fromProto(it) },
        timestamp = toMillis(timedTransaction.timestamp)
    )
}
//fun fromProto(transaction: protoTransaction): Transaction {
//    return Transaction(
//        txId = transaction.txId,
//        inputs = transaction.inputsList,
//        outputs = transaction.outputsList,
//    )
//}

//fun fromProto(transfer: protoTransfer): Transfer {
//    return Transfer(
//        address = transfer.dstAddress,
//        coins = transfer.coins,
//    )
//}

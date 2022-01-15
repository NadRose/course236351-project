package rest_api.repository.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.protobuf.ByteString
import com.google.protobuf.kotlin.toByteStringUtf8
import com.google.protobuf.util.Timestamps
import com.google.protobuf.util.Timestamps.toMillis
import cs236351.transactionManager.*
import cs236351.transactionManager.Transfer as ProtoTransfer
import cs236351.transactionManager.UTxO as ProtoUTxO
import cs236351.transactionManager.Transaction as ProtoTransaction
import cs236351.transactionManager.TimedTransaction as ProtoTimedTransaction
import cs236351.transactionManager.TimedTransactionList as ProtoTimedTransactionList
import cs236351.transactionManager.TransactionList as ProtoTransactionList

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

data class TimedTransactionGRPC(
    val txId: String,
    val inputs: MutableList<ProtoUTxO>,
    val outputs: MutableList<ProtoTransfer>,
    val timestamp: Long
) : Comparable<TimedTransactionGRPC> {

    override operator fun compareTo(other: TimedTransactionGRPC): Int {
        val cmp = (this.timestamp - other.timestamp).toInt()
        if (cmp == 0) {
            return this.txId.compareTo(other.txId)
        }
        return cmp
//        return (this.timestamp - other.timestamp).toInt()
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

val mapper = jacksonObjectMapper()

fun toProto(transaction: Transaction): ProtoTransaction {
    return transaction {
        txId = transaction.txId
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

fun toProto(transfer: Transfer, srcAddress: String, txId: String = ""): ProtoTransfer {
    return transfer {
        this.srcAddress = srcAddress
        dstAddress = transfer.address
        coins = transfer.coins
        if (txId != "") this.txId = txId
    }
}

fun toProto(utxo: UTxO): ProtoUTxO {
    return uTxO { txId = utxo.txId; address = utxo.address }
}

fun toProto(grpcList: List<TimedTransactionGRPC>): ProtoTimedTransactionList {
    return timedTransactionList {
        grpcList.forEach {
            transactionList.add(timedTransaction {
                transaction = transaction {
                    txId = it.txId
                    inputs.addAll(it.inputs)
                    outputs.addAll(it.outputs)
                }
                timestamp = Timestamps.fromMillis(it.timestamp)
            })
        }
    }
}

fun toProto(modelList: List<Transaction>): ProtoTransactionList {
    return transactionList {
        modelList.forEach {
            transactions.add(transaction {
                txId = it.txId
                inputs.addAll(it.inputs.map { i -> toProto(i) })
                outputs.addAll(it.outputs.map { o -> toProto(o, it.inputs[0].address) })
            })
        }
    }
}

fun fromProto(protoTransaction: ProtoTransaction, txId: String, timestamp: Long): TimedTransactionGRPC {
    return TimedTransactionGRPC(
        txId = txId,
        inputs = protoTransaction.inputsList,
        outputs = protoTransaction.outputsList,
        timestamp = timestamp
    )
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

fun toKotlinPureTimedTransaction(tx: TimedTransactionGRPC): TimedTransaction {
    return TimedTransaction(
        txId = tx.txId,
        inputs = tx.inputs.map { fromProto(it) },
        outputs = tx.outputs.map { fromProto(it) },
        timestamp = tx.timestamp,
    )
}

fun fromKotlinPureTimedTransaction(tx: TimedTransaction): TimedTransactionGRPC {
    val srcAddress = tx.inputs[0].address
    return TimedTransactionGRPC(
        txId = tx.txId,
        inputs = tx.inputs.map { toProto(it) }.toMutableList(),
        outputs = tx.outputs.map { toProto(it, srcAddress) }.toMutableList(),
        timestamp = tx.timestamp
    )
}

fun toByteString(grpcList: List<TimedTransactionGRPC>): ByteString {
    val txList = grpcList.map { toKotlinPureTimedTransaction(it) }
    val serialized = mapper.writeValueAsString(txList)
    return serialized.toByteStringUtf8()
}

fun fromByteString(serialized: ByteString): List<TimedTransactionGRPC> {
    val desList: List<TimedTransaction> = mapper.readValue(serialized.toStringUtf8())
    return desList.map { fromKotlinPureTimedTransaction(it) }
}
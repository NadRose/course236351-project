package rpc

import cs236351.transactionManager.*
import rest_api.repository.model.TimedTransactionGRPC
import rest_api.repository.model.fromProto
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

// convert the list to TimedTransactionGRPC and returns an empty list if not valid
fun isValidTxList(transactionList: List<Transaction>): MutableList<TimedTransactionGRPC> {
    val isZeroTxList = transactionList[0].txId == "0"
    val txIdSet = mutableSetOf<String>()
    val res = mutableListOf<TimedTransactionGRPC>()
    for (transaction in transactionList) {
        if (isZeroTxList) {
            if (transaction.txId != "0") {
                return mutableListOf()
            }
        } else {
            if (transaction.txId == "0" || !txIdSet.add(transaction.txId)) return mutableListOf()
            transaction.inputsList.forEach { input ->
                if (!txIdSet.add(input.txId)) return mutableListOf()
            }
        }
        res.add(fromProto(transaction, UUID.randomUUID().toString(), System.currentTimeMillis()))
    }
    return res
}

fun md5(input:String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}
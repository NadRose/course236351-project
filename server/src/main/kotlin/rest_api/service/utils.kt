package rest_api.service

import rest_api.repository.model.Transaction
import rest_api.repository.model.UTxO

fun isValidTxList(transactionList: List<Transaction>): Boolean {
    val isZeroTxList = transactionList[0].txId == "0"
    val txIdSet = mutableSetOf<String>()
    transactionList.forEach { transaction ->
        if (isZeroTxList) {
            if (transaction.txId != "0") return false
        } else {
            if (transaction.txId == "0" || !txIdSet.add(transaction.txId)) return false
            transaction.inputs.forEach { input ->
                if (!txIdSet.add(input.txId)) return false
            }
        }
    }
    return true;
}

fun getAmount(uTxO: UTxO): ULong {
    return transactionsMap[uTxO.address]?.find { transaction ->
        transaction.txId == uTxO.txId
    }?.outputs?.find { transfer -> transfer.address == uTxO.address }?.coins!! // we assume the given utxo is indeed available and exists.
}
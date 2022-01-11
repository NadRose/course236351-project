package rest_api.service

import cs236351.transactionManager.*


fun isValidTxList(transactionList: List<Transaction>): Boolean {
    val isZeroTxList = transactionList[0].txId == "0"
    val txIdSet = mutableSetOf<String>()
    transactionList.forEach { transaction ->
        if (isZeroTxList) {
            if (transaction.txId != "0") return false
        } else {
            if (transaction.txId == "0" || !txIdSet.add(transaction.txId)) return false
            transaction.inputsList.forEach { input ->
                if (!txIdSet.add(input.txId)) return false
            }
        }
    }
    return true;
}
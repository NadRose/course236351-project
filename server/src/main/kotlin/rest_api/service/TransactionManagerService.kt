package rest_api.service

import org.springframework.stereotype.Service
import rest_api.repository.model.RequestBodyType
import rest_api.repository.model.Transaction
import rest_api.repository.model.UTxO
import java.math.BigInteger
import java.util.*

@Service
class TransactionManagerService(){
    fun submitTransaction(body: RequestBodyType): BigInteger {

        return BigInteger.valueOf(1)
    }

    fun submitAtomicTxList(body: RequestBodyType): BigInteger {
        return BigInteger.valueOf(1)
    }

    fun makeTransfer(body: RequestBodyType): String {
        return body.address.toString()
    }

    fun getUTxOs(body: RequestBodyType): List<UTxO> {
        return listOf( UTxO(BigInteger.valueOf(124), BigInteger.valueOf(1243)))
    }

    fun getTxHistory(body: RequestBodyType, limit: Optional<Int>): List<Transaction> {
        return listOf( Transaction(BigInteger.valueOf(124), listOf(), listOf()))
    }

    fun getLedgerHistory(): List<Transaction> {
        return listOf()
    }

}
package rest_api.controller

import rest_api.repository.model.RequestBodyType
import rest_api.repository.model.Transaction
import rest_api.repository.model.UTxO
import java.math.BigInteger
import java.util.Optional
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import rest_api.service.TransactionManagerService

@RestController
class TransactionManagerController(private val transactionManagerService: TransactionManagerService){
    @PostMapping("/submit/transaction")
    fun submitTransaction(@RequestBody body: RequestBodyType): BigInteger = transactionManagerService.submitTransaction(body)

    @PostMapping("/submit/atomic/")
    fun submitAtomicTxList(@RequestBody body: RequestBodyType): BigInteger = transactionManagerService.submitAtomicTxList(body)

    @PostMapping("/transfer")
    fun makeTransfer(@RequestBody body: RequestBodyType): String = transactionManagerService.makeTransfer(body)

    @GetMapping("/utxo")
    fun getUTxOs(@RequestBody body: RequestBodyType): List<UTxO> = transactionManagerService.getUTxOs(body)

    @GetMapping("/tx/history/{limit}")
    fun getTxHistory(@RequestBody body: RequestBodyType, @PathVariable limit: Optional<Int>): List<Transaction> = transactionManagerService.getTxHistory(body, limit)

    @GetMapping("/history")
    fun getLedgerHistory(): List<Transaction> = transactionManagerService.getLedgerHistory()

}
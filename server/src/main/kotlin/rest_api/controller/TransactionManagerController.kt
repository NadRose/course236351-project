package rest_api.controller

import rest_api.repository.model.Transaction
import rest_api.repository.model.UTxO
import java.util.Optional
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import rest_api.repository.model.Transfer
import rest_api.service.TransactionManagerService

@RestController
class TransactionManagerController(private val transactionManagerService: TransactionManagerService){
    @PostMapping("/submit/transaction/{address}")
    fun submitTransaction(@RequestBody body: Transaction, @PathVariable address: String): String = transactionManagerService.submitTransaction(body, address)

    @PostMapping("/submit/atomic/{address}")
    fun submitAtomicTxList(@RequestBody body: List<Transaction>, @PathVariable address: String): String = transactionManagerService.submitAtomicTxList(body, address)

    @PostMapping("/transfer/{address}")
    fun makeTransfer(@RequestBody body: Transfer, @PathVariable address: String): String = transactionManagerService.makeTransfer(body, address)

    @GetMapping("/utxo/{address}")
    fun getUTxOs(@PathVariable address: String): List<UTxO> = transactionManagerService.getUTxOs(address)

    @GetMapping("/tx/history/{address}/{limit}")
    fun getTxHistory(@PathVariable address: String, @PathVariable limit: Optional<Int>): List<Transaction> = transactionManagerService.getTxHistory(address, limit)

    @GetMapping("/history")
    fun getLedgerHistory(): List<Transaction> = transactionManagerService.getLedgerHistory()

}
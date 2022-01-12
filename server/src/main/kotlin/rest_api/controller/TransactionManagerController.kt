package rest_api.controller

import org.springframework.web.bind.annotation.*
import rest_api.repository.model.*
import rest_api.service.TransactionManagerService

@RestController
class TransactionManagerController(private val transactionManagerService: TransactionManagerService) {

    @PostMapping("/submit/transaction/{address}")
    fun submitTransaction(@RequestBody body: Transaction, @PathVariable address: String): String =
        transactionManagerService.submitTransaction(body, address)

//    @PostMapping("/submit/atomic/{address}")
//    fun submitAtomicTxList(@RequestBody body: List<Transaction>, @PathVariable address: String): String = transactionManagerService.submitAtomicTxList(body, address)

    @PostMapping("/transfer/{address}")
    fun makeTransfer(@RequestBody body: Transfer, @PathVariable address: String): String = transactionManagerService.makeTransfer(body, address)

    @GetMapping("/utxo/{address}")
    fun getUTxOs(@PathVariable address: String): List<UTxO> = transactionManagerService.getUTxOs(address)

    @GetMapping("/tx/history/{address}")
    fun getTxHistory(@PathVariable address: String, @RequestParam limit: Int?): List<TimedTransaction> = transactionManagerService.getTxHistory(address, limit)

//    @GetMapping("/history/")
//    fun getLedgerHistory( @RequestParam limit: Int?): List<Transaction> = transactionManagerService.getLedgerHistory(limit)

}
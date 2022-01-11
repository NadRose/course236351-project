package rpc

import com.google.protobuf.util.Timestamps.fromMillis
import cs236351.transactionManager.*
import cs236351.transactionManager.TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineImplBase
import java.util.*

class TransactionManagerRPCService : TransactionManagerServiceCoroutineImplBase() {
    private var UTxOPool: HashMap<String, MutableList<UTxO>> = hashMapOf(
        Pair("1", mutableListOf(uTxO { txId = "1"; address = "1" })),
        Pair("2", mutableListOf(uTxO { txId = "2"; address = "2" })),
    )
    private var MissingUTxOPool: HashMap<String, MutableList<UTxO>> = hashMapOf()
    private var transactionsMap: HashMap<String, SortedSet<rest_api.repository.model.TimedTransaction>> = hashMapOf(
        Pair(
            "1",
            sortedSetOf(
                rest_api.repository.model.TimedTransaction(
                    txId = "1",
                    inputs = mutableListOf(
                        uTxO { txId = "0"; address = "0" }
                    ),
                    outputs = mutableListOf(
                        transfer { srcAddress = "0"; dstAddress = "1"; coins = 20 },
                        transfer { srcAddress = "0"; dstAddress = "2"; coins = 12 },
                        transfer { srcAddress = "0"; dstAddress = "0"; coins = Long.MAX_VALUE - 32 },
                    ),
                    timestamp = System.currentTimeMillis()
                ),
            ),
        ),
    )

    override suspend fun submitTransaction(request: Transaction): Response {
        println(request.toString())
        val inputTxId = if (request.txId == "0") UUID.randomUUID() else request.txId
        val srcAdd = request.inputsList[0].address
        val inputs: MutableList<UTxO> = mutableListOf()
        val outputs: MutableList<Transfer> = mutableListOf()
        for (input in request.inputsList) {
            if (this.UTxOPool[input.address]?.remove(input) == false) {
                this.MissingUTxOPool.getOrPut(input.address) {
                    mutableListOf() // TODO what happens if leader crashes in here? how will the followers know he crashed and process the request?
                }.add(input)
            }
            inputs.add(uTxO { txId = input.txId; address = input.address })
        }
        for (output in request.outputsList) {
            outputs.add(transfer {
                srcAddress = output.srcAddress; dstAddress = output.dstAddress; coins = output.coins
            })
        }
        val tx = rest_api.repository.model.TimedTransaction(
            inputTxId.toString(),
            inputs,
            outputs,
            System.currentTimeMillis()
        )
        transactionsMap.getOrPut(srcAdd) {
            sortedSetOf()
        }.add(tx)

        // debugging like assholes
        println(UTxOPool.toString())
        println(MissingUTxOPool.toString())
        println(transactionsMap.forEach { entry ->
            println("address: " + entry.key)
            entry.value.forEach {
                println("value: ")
                it.printTT()
            }
        })


        //TODO Submit tx to ledger using paxos/atomic broadcast or some shit.

        return response { type = ResponseEnum.SUCCESS; message = tx.txId }
    }

    override suspend fun makeTransfer(request: Transfer): Response {

        val inputTxId = UUID.randomUUID()
        val outputs = mutableListOf(request)
        val inputs: MutableList<UTxO> = mutableListOf()
        var curSum: Long = 0

        if (!this.UTxOPool.containsKey(request.srcAddress)) {
            return response { type = ResponseEnum.FAILURE; message = "Operation failed! Not enough available UTxOs." }
        }

        val utxosToRemove: MutableList<UTxO> = mutableListOf()
        for (utxo in this.UTxOPool[request.srcAddress].orEmpty()) {
            if (curSum < request.coins) {
                inputs.add(utxo)
                curSum += getAmount(utxo)
                utxosToRemove.add(utxo)
            } else break
        }
        if (curSum > request.coins) {
            this.UTxOPool[request.srcAddress]?.add(uTxO {
                txId = inputTxId.toString()
                address = request.srcAddress
            })
        }
        this.UTxOPool[request.srcAddress]?.removeAll(utxosToRemove)

        val tx = rest_api.repository.model.TimedTransaction(
            inputTxId.toString(),
            inputs,
            outputs,
            System.currentTimeMillis()
        )
        transactionsMap.getOrPut(request.srcAddress) {
            sortedSetOf()
        }.add(tx)

        //TODO Submit tx to ledger using paxos/atomic broadcast or some shit.

        return response { type = ResponseEnum.SUCCESS; message = tx.txId }
    }

    override suspend fun getUTxOs(request: AddressRequest): UTxOList {
        return uTxOList {
            utxoList.addAll(UTxOPool[request.address].orEmpty())
        }
    }

    override suspend fun getTxHistory(request: AddressRequest): TimedTransactionList {
        return timedTransactionList {
            val slicedList = transactionsMap[request.address]?.take(request.limit)
            slicedList?.forEach { //TODO check if limit or sliced list is null what happens?
                transactionList.add(timedTransaction {
                    transaction = transaction {
                        txId = it.txId;
                        inputs.addAll(it.inputs)
                        outputs.addAll(it.outputs)
                    }
                    timestamp = fromMillis(it.timestamp);
                })
            }
        }
    }

    private fun getAmount(uTxO: UTxO): Long {
        return transactionsMap[uTxO.address]?.find {
            it.txId == uTxO.txId
        }?.outputs?.find { transfer -> transfer.dstAddress == uTxO.address }?.coins!! // we assume the given utxo is indeed available and exists.
    }
}
package rpc

import cs236351.transactionManager.*
import cs236351.transactionManager.TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineImplBase
import kotlinx.coroutines.channels.Channel
import main.shards
import multipaxos.AtomicBroadcast
import multipaxos.Proposer
import rest_api.repository.model.fromProto
import rest_api.repository.model.toProto
import zookeeper.kotlin.zookeeper.ZooKeeperKt
import java.util.*
import rest_api.repository.model.TimedTransactionGRPC as ModelTimedTransaction

val utxoPool: HashMap<String, MutableList<UTxO>> = hashMapOf(
    Pair("0", mutableListOf(uTxO { txId = "0"; address = "0" })),
)
val missingUtxoPool: HashMap<String, MutableList<UTxO>> = hashMapOf()
val transactionsMap: HashMap<String, MutableSet<ModelTimedTransaction>> = hashMapOf(
    Pair(
        "0",
        sortedSetOf(
            ModelTimedTransaction(
                txId = "0",
                inputs = mutableListOf(),
                outputs = mutableListOf(
                    transfer { srcAddress = "0"; dstAddress = "0"; coins = Long.MAX_VALUE },
                ),
                timestamp = System.currentTimeMillis()
            ),
        ),
    ),
)

fun findOwnerShard(address: String): String {
    val shardNum = address.toBigInteger().mod(shards.size.toBigInteger()).toInt()
    return shards[shardNum]
}

class TransactionManagerRPCService(
    private val zkClient: ZooKeeperKt,
    private val proposer: Proposer,
    private val atomicBroadcast: AtomicBroadcast<List<ModelTimedTransaction>>
) : TransactionManagerServiceCoroutineImplBase() {

    private val ledger = sortedSetOf<ModelTimedTransaction>()
    private var senders = mutableSetOf<String>()
    private val ledgerChan = Channel<String>(1)

    override suspend fun findOwner(request: AddressRequest): AddressRequest {
        val shard = findOwnerShard(request.address)
        return addressRequest {
            address = zkClient.getChildren("/$shard") {}.first[0]
        }
    }

    override suspend fun consensusAddProposal(request: ConsensusMessage): Response {
        proposer.addProposal(request.message)
        return response {
            type = ResponseEnum.SUCCESS
        }
    }

    override suspend fun consensusPostLedger(request: TimedTransactionList): Response {
        val lst = fromProto(request)
        val senderName = lst[0].txId
        if (senderName in senders)
            return response {}
        val smallLedger = lst.toMutableList()
        smallLedger.removeAt(0)
        ledger.addAll(smallLedger)
        ledgerChan.send(senderName)
        return response {}
    }

    override suspend fun submitTransaction(request: Transaction): Response {
        val inputTxId = request.txId
        if (isTxExist(inputTxId, request.inputsList[0].address))
            return response { type = ResponseEnum.SUCCESS; message = inputTxId }
        val srcAdd = request.inputsList[0].address
        val inputs: MutableList<UTxO> = mutableListOf()
        val outputs: MutableList<Transfer> = mutableListOf()
        for (input in request.inputsList) {
            if (!utxoPool.getOrElse(input.address) { mutableListOf() }.remove(input)) {
                missingUtxoPool.getOrPut(input.address) {
                    mutableListOf()
                }.add(input)
            }
            inputs.add(uTxO { txId = input.txId; address = input.address })
        }
        for (output in request.outputsList) {
            outputs.add(transfer {
                srcAddress = output.srcAddress; dstAddress = output.dstAddress; coins = output.coins
            })
        }
        val tx = ModelTimedTransaction(
            inputTxId.toString(),
            inputs,
            outputs,
            System.currentTimeMillis()
        )
        transactionsMap.getOrPut(srcAdd) {
            sortedSetOf()
        }.add(tx)

        atomicBroadcast.send(listOf(tx))

        return response { type = ResponseEnum.SUCCESS; message = tx.txId }
    }

    override suspend fun makeTransfer(request: Transfer): Response {
        val inputTxId = request.txId
        if (isTxExist(inputTxId, request.srcAddress))
            return response { type = ResponseEnum.SUCCESS; message = inputTxId }
        val outputs = mutableListOf(request)
        val inputs: MutableList<UTxO> = mutableListOf()
        var curSum: Long = 0

        if (!utxoPool.containsKey(request.srcAddress)) {
            return response { type = ResponseEnum.FAILURE; message = "Operation failed! Not enough available UTxOs." }
        }

        val utxoToRemove: MutableList<UTxO> = mutableListOf()
        for (utxo in utxoPool[request.srcAddress].orEmpty()) {
            if (curSum < request.coins) {
                inputs.add(utxo)
                curSum += getAmount(utxo)
                utxoToRemove.add(utxo)
            } else break
        }
        if (curSum > request.coins) {
            utxoPool[request.srcAddress]!!.add(uTxO {
                txId = inputTxId.toString()
                address = request.srcAddress
            })
        }

        utxoPool[request.srcAddress]!!.removeAll(utxoToRemove)

        val tx = ModelTimedTransaction(
            inputTxId.toString(),
            inputs,
            outputs,
            System.currentTimeMillis()
        )
        transactionsMap.getOrPut(request.srcAddress) {
            sortedSetOf()
        }.add(tx)

        atomicBroadcast.send(listOf(tx))

        return response { type = ResponseEnum.SUCCESS; message = tx.txId }
    }

    override suspend fun submitAtomicTransaction(request: TransactionList): Response {
        // We assume clients are honest and therefore support "zero transactions list" (all tx-id's are "0")
        // under the assumption that all input utxo are valid and unrelated - meaning can be submitted atomically
        // or "Non-zero transaction list (all tx-id's are valid uuid)
        val txList = isValidTxList(request.transactionsList)
        if (isValidTxList(request.transactionsList).isEmpty()) {
            return response {
                type = ResponseEnum.FAILURE
                message = "Failed to submit Tx list, list is not commit-able"
            }
        }
        atomicBroadcast.send(txList)
        return response {
            type = ResponseEnum.SUCCESS
            message = "Success submitting Tx list"
        }
    }

    override suspend fun getUTxOs(request: AddressRequest): UTxOList {
        return uTxOList {
            utxoList.addAll(utxoPool[request.address].orEmpty())
        }
    }

    override suspend fun getTxHistory(request: AddressRequest): TimedTransactionList {
        val slicedList = transactionsMap.getOrElse(request.address) { sortedSetOf() }.take(request.limit)
        return toProto(slicedList)
    }

    override suspend fun getLedger(request: AddressRequest): TimedTransactionList {
        val tx = ModelTimedTransaction(
            request.address,
            mutableListOf(),
            mutableListOf(),
            0,
        )
        atomicBroadcast.send(listOf(tx))
        senders = mutableSetOf()

        for (shard in ledgerChan) {
            senders.add(shard)
            if (senders.size == shards.size) break
        }
        val slicedList = ledger.take(request.limit)
        return toProto(slicedList)
    }

    private fun getAmount(uTxO: UTxO): Long {
        return transactionsMap.getOrElse(uTxO.address) {
            sortedSetOf()
        }.find {
            it.txId == uTxO.txId
        }!!.outputs.find { transfer ->
            transfer.dstAddress == uTxO.address
        }!!.coins
    }

    private fun isTxExist(txId: String, address: String): Boolean {
        val elem = transactionsMap.getOrElse(address) {
            sortedSetOf()
        }.find { it.txId == txId }

        elem?.let {
            return true
        }
        return false
    }
}

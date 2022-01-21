package rpc

import cs236351.transactionManager.*
import cs236351.transactionManager.TransactionManagerServiceGrpcKt.TransactionManagerServiceCoroutineImplBase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import multipaxos.AtomicBroadcast
import multipaxos.Proposer
import rest_api.*
import rest_api.repository.model.fromByteString
import rest_api.repository.model.fromProto
import rest_api.repository.model.toByteString
import rest_api.repository.model.toProto
import zookeeper.kotlin.zookeeper.ZooKeeperKt
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timer
import rest_api.repository.model.TimedTransactionGRPC as ModelTimedTransaction

val utxoPool: HashMap<String, MutableList<UTxO>> =
    if (membershipName == "SHARD_1")
        hashMapOf(
            Pair("0", mutableListOf(uTxO { txId = "0"; address = "0" })),
        )
    else hashMapOf()

val missingUtxoPool: HashMap<String, MutableList<UTxO>> = hashMapOf()
val transactionsMap: HashMap<String, MutableSet<ModelTimedTransaction>> =
    if (membershipName == "SHARD_1")
        hashMapOf(
            Pair(
                "0",
                sortedSetOf(
                    ModelTimedTransaction(
                        txId = "0",
                        inputs = mutableListOf(),
                        outputs = mutableListOf(
                            transfer { dstAddress = "0"; coins = Long.MAX_VALUE },
                        ),
                        timestamp = System.currentTimeMillis()
                    ),
                ),
            ),
        )
    else hashMapOf()

val proposedMessages = mutableListOf<ModelTimedTransaction>()

fun findOwnerShard(address: String): String {
    val shardNum = address.toBigInteger().mod(shards.size.toBigInteger()).toInt()
    return shards[shardNum]
}

class TransactionManagerRPCService(
    private val zkClient: ZooKeeperKt,
    private val proposer: Proposer,
    private val atomicBroadcast: AtomicBroadcast<List<ModelTimedTransaction>>
) : TransactionManagerServiceCoroutineImplBase() {

    val timer = Timer().schedule(delay = 0, period = 1000){
        runBlocking {
            if (proposedMessages.isNotEmpty()) {
                proposer.addProposal(toByteString(proposedMessages))
                println("Messages proposed.")
                proposedMessages.clear()
            }
        }
    }

    override suspend fun findOwner(request: AddressRequest): AddressRequest {
        val shard = findOwnerShard(request.address)
        return addressRequest {
            address = zkClient.getChildren("/$shard") {}.first[0]
        }
    }

    override suspend fun consensusAddProposal(request: ConsensusMessage): Response {
        println("proposer $serverAddress has received a message.")
        val proposedMessage = fromByteString(request.message)
        proposedMessages.addAll(proposedMessage)
        println("proposer $serverAddress added message to queue.")
        return response {
            type = ResponseEnum.SUCCESS
        }
    }

    override suspend fun submitTransaction(request: Transaction): Response {
        if (extractServerId(serverAddress) == 1){
            println("$serverAddress going to sleep...")
            Thread.sleep(8000)
        }
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
            return response { type = ResponseEnum.SUCCESS; message = "inputTxId $inputTxId exists" }
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
            outputs.add(transfer {
                srcAddress = request.srcAddress
                dstAddress = request.srcAddress
                coins = curSum - request.coins
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
        if (txList.isEmpty()) {
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
        if (request.address == "All") {
            val ownLedger = mutableListOf<ModelTimedTransaction>()
            transactionsMap.values.forEach {
                ownLedger.addAll(it)
            }
            return toProto(ownLedger)
        }
        val slicedList = transactionsMap.getOrElse(request.address) { sortedSetOf() }.take(request.limit)
        return toProto(slicedList)
    }

    override suspend fun getLedger(request: AddressRequest): TimedTransactionList {
        val transactionSet = sortedSetOf<ModelTimedTransaction>()
        transactionsMap.mapValues { transactionSet.addAll(it.value) }
        shards.forEach {
            if (it != membershipName) {
                val ownerAddress = zkClient.getChildren("/$it") {}.first[0]
                val ownerStub = stubMap[ownerAddress]!!
                val requestToSend = addressRequest { address = "All" }
                val smallLedger = fromProto(ownerStub.getTxHistory(requestToSend))
                transactionSet.addAll(smallLedger)
            }
        }
        val slicedList = transactionSet.take(request.limit)
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

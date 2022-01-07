package rest_api.repository.model

data class Transfer (
    val address: String,
    val coins: ULong
)

data class UTxO(
    val txId: String,
    val address: String,
)

open class Transaction(
    val txId: String,
    val inputs: MutableList<UTxO>,
    val outputs: MutableList<Transfer>
)

class TimedTransaction(txId: String, inputs: MutableList<UTxO>, outputs: MutableList<Transfer>, val timestamp: Long) :
    Transaction(txId, inputs, outputs)
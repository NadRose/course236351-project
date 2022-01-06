package rest_api.repository.model

import java.util.*

data class Transfer (
    val address: String,
    val coins: ULong
)

data class UTxO(
    val txId: String,
    val address: String,
    val amount: ULong = 0u
)

data class Transaction(
    val txId: String,
    val inputs: MutableList<UTxO>,
    val outputs: MutableList<Transfer>
)

data class UTxOPoolItem(
    val txId: String,
    val amount: Long
)
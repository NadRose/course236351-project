package rest_api.repository.model

import java.math.BigInteger
import java.util.*

data class Transfer (
    val address: BigInteger,
    val coins: ULong
)

data class UTxO(
    val txId: BigInteger,
    val address: BigInteger
)

data class Transaction(
    val txId: BigInteger,
    val inputs: List<UTxO>,
    val outputs: List<Transfer>
)

data class RequestBodyType(
    val address: BigInteger,
    val payload: Optional<Any>
)
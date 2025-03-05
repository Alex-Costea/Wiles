package wiles.shared.data

data class OutputData(
    val output : String,
    val exceptionsString : String,
    val exceptions : WilesExceptionsCollection,
)
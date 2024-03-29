package wiles.shared

data class OutputData(
    val output : String,
    val exceptionsString : String,
    val exceptions : CompilationExceptionsCollection,
    val syntax : JSONStatement? = null
)
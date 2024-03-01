package wiles.shared

data class CommandLineArgs(
    val isDebug : Boolean,
    val isCompileCommand: Boolean,
    val isRunCommand : Boolean,
    val filename : String?,
    val code : String?
)
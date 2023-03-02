package wiles.shared.constants

object CommandLineArguments {
    const val DEBUG_COMMAND = "--debug"
    const val NO_INPUT_FILE_COMMAND = "--nofile"
    const val COMPILE_COMMAND = "--compile"
    const val RUN_COMMAND = "--run"

    val CL_ARGS = listOf(DEBUG_COMMAND, NO_INPUT_FILE_COMMAND, COMPILE_COMMAND, RUN_COMMAND)
}
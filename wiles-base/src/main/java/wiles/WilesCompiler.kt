package wiles

import wiles.checker.Checker
import wiles.checker.data.CheckerContext
import wiles.interpreter.Interpreter
import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.exceptions.PanicException
import wiles.parser.Parser
import wiles.shared.*
import wiles.shared.constants.CommandLineArguments.CODE_COMMAND
import wiles.shared.constants.CommandLineArguments.COMPILE_COMMAND
import wiles.shared.constants.CommandLineArguments.DEBUG_COMMAND
import wiles.shared.constants.CommandLineArguments.FILE_COMMAND
import wiles.shared.constants.CommandLineArguments.INPUT_COMMAND
import wiles.shared.constants.CommandLineArguments.RUN_COMMAND
import wiles.shared.constants.ErrorMessages.COMPILATION_FAILED_ERROR
import wiles.shared.constants.ErrorMessages.LINE_SYMBOL
import wiles.shared.constants.ErrorMessages.RED_TEXT_END_ERROR
import wiles.shared.constants.ErrorMessages.RED_TEXT_START_ERROR
import wiles.shared.constants.Settings.OBJECT_FILE
import java.io.ByteArrayInputStream
import java.io.FileWriter
import java.io.IOException
import java.util.*


object WilesCompiler {

    private fun getErrorsDisplay(exceptions: CompilationExceptionsCollection, input: String, debug : Boolean) : String
    {
        if (exceptions.size > 0)
            return RED_TEXT_START_ERROR+ run {
                val optional =
                    exceptions.sortedWith(nullsLast(compareBy<AbstractCompilationException> { it.tokenLocation.line }
                        .thenBy { it.tokenLocation.lineIndex }))
                        .map {
                            LINE_SYMBOL + "Line ${it.tokenLocation.line}: " + it.message +
                                    it.tokenLocation.displayLocation(
                                        input) + (if (debug) "\n" + it.stackTraceToString() else "")
                        }
                        .fold("") { a, b -> a + b }
                if (optional.isEmpty())
                    throw InternalErrorException()
                COMPILATION_FAILED_ERROR + optional
            } +RED_TEXT_END_ERROR
        else return ""
    }

    private fun getCommandLine(args: Array<String>): CommandLineArgs {
        var debug = false
        var compileCommand = false
        var runCommand = false
        var filename : String? = null
        var code : String? = null
        var inputText : String? = null
        for(arg in args)
        {
            if(arg == DEBUG_COMMAND)
                debug = true
            if(arg == COMPILE_COMMAND)
                compileCommand = true
            if(arg == RUN_COMMAND)
                runCommand = true
            if(arg.startsWith(FILE_COMMAND))
                filename = arg.split(FILE_COMMAND, limit = 2)[1]
            if(arg.startsWith(CODE_COMMAND))
                code = arg.split(CODE_COMMAND, limit = 2)[1]
            if(arg.startsWith(INPUT_COMMAND))
                inputText = arg.split(INPUT_COMMAND, limit = 2)[1]
        }
        return CommandLineArgs(
            isDebug = debug,
            isCompileCommand = compileCommand,
            isRunCommand = runCommand,
            filename = filename,
            code = code,
            inputText = inputText
        )
    }

    @JvmStatic
    fun getOutput(args: Array<String>) : OutputData
    {
        //args
        val exceptions = CompilationExceptionsCollection()
        var interpreterCode : String? = null
        val exceptionsString = StringBuilder()
        val clArgs = getCommandLine(args)

        if(clArgs.filename == null && clArgs.code == null)
            throw Exception("Invalid arguments!")

        //get input
        val scanner = if(clArgs.inputText == null)
            Scanner(System.`in`)
        else Scanner(ByteArrayInputStream(clArgs.inputText.toByteArray(Charsets.UTF_8)))

        if(!clArgs.isRunCommand) {
            val parser = Parser(clArgs.code, clArgs.isDebug, clArgs.filename)
            exceptions.addAll(parser.getExceptions())
            val result = parser.getResults()

            if (clArgs.isDebug) {
                print("Syntax tree: ")
                println(result)
            }

            if (exceptions.isNotEmpty()) {
                exceptionsString.append(getErrorsDisplay(exceptions, parser.input, clArgs.isDebug))
                return OutputData(output = "",
                    exceptionsString = exceptionsString.toString(),
                    exceptions = exceptions)
            }

            val checker = Checker(if (clArgs.isDebug) null else parser.json, CheckerContext(0))
            exceptions.addAll(checker.check())

            if (clArgs.isDebug) {
                print("After checking: ")
                println(checker.code)
            }

            if (clArgs.isCompileCommand && exceptions.isEmpty()) {
                val writer = FileWriter((clArgs.filename ?: "code.wiles") + OBJECT_FILE)
                writer.write(checker.codeAsJSONString)
                writer.close()
            }

            exceptionsString.append(getErrorsDisplay(exceptions, parser.input, clArgs.isDebug))

            interpreterCode = checker.codeAsJSONString
        }

        val output = StringBuilder()
        if(!clArgs.isCompileCommand && exceptions.isEmpty())
        {
            val interpreter = Interpreter(interpreterCode, clArgs.isDebug,
                filename = clArgs.filename?: "code.wiles",
                InterpreterContext(scanner, output))
            try{
                interpreter.interpret()
            }
            catch (ex : PanicException)
            {
                val message = ex.message ?:"A runtime error occurred!"
                exceptions.addLast(PanicExceptionWithLocation(message, ex.location ?: TokenLocation())
                )
                exceptionsString.append(message)
            }
        }
        return OutputData(
            output = output.toString(),
            exceptionsString = exceptionsString.toString(),
            exceptions = exceptions)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val result = getOutput(args)
        System.err.print(result.exceptionsString)
        print(result.output)
    }
}
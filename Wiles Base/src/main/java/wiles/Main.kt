package wiles

import wiles.checker.Checker
import wiles.interpreter.Interpreter
import wiles.interpreter.Interpreter.Companion.SCANNER
import wiles.parser.Parser
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
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


object Main {

    private fun printExceptions(exceptions: CompilationExceptionsCollection, input: String, additionalLines: Int,
                                DEBUG : Boolean)
    {
        if (exceptions.size > 0)
            System.err.println(RED_TEXT_START_ERROR+ run {
                val optional =
                    exceptions.sortedWith(nullsLast(compareBy<AbstractCompilationException> { it.tokenLocation.line }
                        .thenBy { it.tokenLocation.lineIndex }))
                        .map {
                            LINE_SYMBOL + "Line ${it.tokenLocation.line}: " + it.message +
                                    it.tokenLocation.displayLocation(
                                        input,
                                        additionalLines
                                    ) + (if (DEBUG) "\n" + it.stackTraceToString() else "")
                        }
                        .fold("") { a, b -> a + b }
                if (optional.isEmpty())
                    throw InternalErrorException()
                COMPILATION_FAILED_ERROR + optional
            }
                    +RED_TEXT_END_ERROR)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        //args
        val DEBUG: Boolean = args.contains(DEBUG_COMMAND)
        val writeCompileFile = args.contains(COMPILE_COMMAND)
        val runCommand = args.contains(RUN_COMMAND)
        val exceptions = CompilationExceptionsCollection()
        var interpreterCode : String? = null
        var filename = args.lastOrNull{it.startsWith(FILE_COMMAND)}?.split(FILE_COMMAND)?.get(1)
        val code = args.lastOrNull{it.startsWith(CODE_COMMAND)}?.split(CODE_COMMAND)?.get(1)

        if(filename == null && code == null)
            throw Exception("Invalid arguments!")

        //get input
        val inputText = args.firstOrNull{it.startsWith(INPUT_COMMAND)}?.split(INPUT_COMMAND)?.get(1)
        SCANNER = if(inputText == null)
            Scanner(System.`in`)
        else Scanner(ByteArrayInputStream(inputText.toByteArray(Charsets.UTF_8)))

        if(!runCommand) {
            val parser = Parser(code, DEBUG, filename)
            if (filename == null)
                filename = "code.wiles"
            exceptions.addAll(parser.getExceptions())
            val result = parser.getResults()

            if (DEBUG) {
                print("Syntax tree: ")
                println(result)
            }

            if (exceptions.isNotEmpty()) {
                printExceptions(exceptions, parser.input, parser.additionalLines, DEBUG)
                return
            }

            val checker = Checker(if (DEBUG) null else parser.json)
            exceptions.addAll(checker.check())

            if (DEBUG) {
                print("After checking: ")
                println(checker.code)
            }

            if (writeCompileFile && exceptions.isEmpty()) {
                val writer = FileWriter(filename + OBJECT_FILE)
                writer.write(checker.codeAsJSONString)
                writer.close()
            }

            printExceptions(exceptions, parser.input, parser.additionalLines, DEBUG)

            interpreterCode = checker.codeAsJSONString
        }

        if(!writeCompileFile && exceptions.isEmpty())
        {
            val interpreter = Interpreter(interpreterCode, DEBUG, filename!!)
            interpreter.interpret()
        }
    }
}
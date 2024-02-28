package wiles

import wiles.checker.Checker
import wiles.checker.data.CheckerContext
import wiles.interpreter.Interpreter
import wiles.interpreter.data.InterpreterContext
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


object WilesCompiler {

    private fun getErrorsDisplay(exceptions: CompilationExceptionsCollection, input: String, additionalLines: Int,
                                 debug : Boolean) : String
    {
        if (exceptions.size > 0)
            return RED_TEXT_START_ERROR+ run {
                val optional =
                    exceptions.sortedWith(nullsLast(compareBy<AbstractCompilationException> { it.tokenLocation.line }
                        .thenBy { it.tokenLocation.lineIndex }))
                        .map {
                            LINE_SYMBOL + "Line ${it.tokenLocation.line}: " + it.message +
                                    it.tokenLocation.displayLocation(
                                        input,
                                        additionalLines
                                    ) + (if (debug) "\n" + it.stackTraceToString() else "")
                        }
                        .fold("") { a, b -> a + b }
                if (optional.isEmpty())
                    throw InternalErrorException()
                COMPILATION_FAILED_ERROR + optional
            } +RED_TEXT_END_ERROR
        else return ""
    }

    @JvmStatic
    fun getOutput(args: Array<String>) : Pair<String,String>
    {
        //args
        val debug: Boolean = args.contains(DEBUG_COMMAND)
        val writeCompileFile = args.contains(COMPILE_COMMAND)
        val runCommand = args.contains(RUN_COMMAND)
        val exceptions = CompilationExceptionsCollection()
        var interpreterCode : String? = null
        var filename = args.lastOrNull{it.startsWith(FILE_COMMAND)}?.split(FILE_COMMAND)?.get(1)
        val code = args.lastOrNull{it.startsWith(CODE_COMMAND)}?.split(CODE_COMMAND)?.get(1)
        val exceptionsString = StringBuilder()

        if(filename == null && code == null)
            throw Exception("Invalid arguments!")

        //get input
        val inputText = args.firstOrNull{it.startsWith(INPUT_COMMAND)}?.split(INPUT_COMMAND)?.get(1)
        val scanner = if(inputText == null)
            Scanner(System.`in`)
        else Scanner(ByteArrayInputStream(inputText.toByteArray(Charsets.UTF_8)))

        if(!runCommand) {
            val parser = Parser(code, debug, filename)
            if (filename == null)
                filename = "code.wiles"
            exceptions.addAll(parser.getExceptions())
            val result = parser.getResults()

            if (debug) {
                print("Syntax tree: ")
                println(result)
            }

            if (exceptions.isNotEmpty()) {
                exceptionsString.append(getErrorsDisplay(exceptions, parser.input, parser.additionalLines, debug))
                return Pair("", exceptionsString.toString())
            }

            val checker = Checker(if (debug) null else parser.json, CheckerContext(0))
            exceptions.addAll(checker.check())

            if (debug) {
                print("After checking: ")
                println(checker.code)
            }

            if (writeCompileFile && exceptions.isEmpty()) {
                val writer = FileWriter(filename + OBJECT_FILE)
                writer.write(checker.codeAsJSONString)
                writer.close()
            }

            exceptionsString.append(getErrorsDisplay(exceptions, parser.input, parser.additionalLines, debug))

            interpreterCode = checker.codeAsJSONString
        }

        val output = StringBuilder()
        if(!writeCompileFile && exceptions.isEmpty())
        {
            val interpreter = Interpreter(interpreterCode, debug, filename!!, InterpreterContext(scanner, output, exceptionsString))
            interpreter.interpret()
        }
        return Pair(output.toString(),exceptionsString.toString())
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val result = getOutput(args)
        System.err.print(result.second)
        print(result.first)
    }
}
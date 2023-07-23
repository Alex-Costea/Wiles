package wiles

import wiles.checker.Checker
import wiles.interpreter.Interpreter
import wiles.parser.Parser
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.constants.CommandLineArguments
import wiles.shared.constants.CommandLineArguments.COMPILE_COMMAND
import wiles.shared.constants.CommandLineArguments.DEBUG_COMMAND
import wiles.shared.constants.CommandLineArguments.NO_INPUT_FILE_COMMAND
import wiles.shared.constants.CommandLineArguments.RUN_COMMAND
import wiles.shared.constants.ErrorMessages.RED_TEXT_END_ERROR
import wiles.shared.constants.ErrorMessages.RED_TEXT_START_ERROR
import wiles.shared.constants.Settings.OBJECT_FILE
import java.io.FileWriter
import java.io.IOException


object Main {

    private lateinit var finalCode : String
    var DEBUG = false
    lateinit var filename : String

    private fun printExceptions(exceptions: CompilationExceptionsCollection, input: String, additionalLines: Int)
    {
        if (exceptions.size > 0)
            System.err.println(RED_TEXT_START_ERROR+exceptions.getExceptionsString(input,additionalLines)
                    +RED_TEXT_END_ERROR)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        //args
        if(args.filter{it !in CommandLineArguments.CL_ARGS }.size>1)
            throw Exception("Invalid args!")
        DEBUG = args.contains(DEBUG_COMMAND)
        val noFile = args.contains(NO_INPUT_FILE_COMMAND)
        val writeCompileFile = args.contains(COMPILE_COMMAND)
        val runCommand = args.contains(RUN_COMMAND)
        val exceptions = CompilationExceptionsCollection()
        var interpreterCode : String? = null

        //or possibly just the code itself
        filename = args.lastOrNull{!it.startsWith("--")} ?: throw Exception("Filename expected!")

        if(!runCommand) {
            val parser = Parser(if (noFile) filename else null)
            if (noFile)
                filename = "code.wiles"
            exceptions.addAll(parser.getExceptions())
            val result = parser.getResults()

            if (DEBUG) {
                print("Syntax tree: ")
                println(result)
            }

            if (exceptions.isNotEmpty()) {
                printExceptions(exceptions, parser.input, parser.additionalLines)
                return
            }

            val checker = Checker(if (DEBUG) null else parser.json)
            exceptions.addAll(checker.check())

            if (DEBUG) {
                print("After checking: ")
                println(checker.code)
            }

            if (writeCompileFile && !noFile && exceptions.isEmpty()) {
                val writer = FileWriter(filename + OBJECT_FILE)
                writer.write(checker.codeAsJSONString)
                writer.close()
            }

            printExceptions(exceptions, parser.input, parser.additionalLines)
            finalCode = checker.code.toString()

            interpreterCode = checker.codeAsJSONString
        }

        if(!writeCompileFile && exceptions.isEmpty())
        {
            val interpreter = Interpreter(interpreterCode)
            interpreter.interpret()
        }
    }
}
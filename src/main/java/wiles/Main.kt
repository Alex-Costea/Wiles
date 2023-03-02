package wiles

import wiles.checker.Checker
import wiles.interpreter.Interpreter
import wiles.parser.Parser
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.constants.CommandLineArguments.COMPILE_COMMAND
import wiles.shared.constants.CommandLineArguments.DEBUG_COMMAND
import wiles.shared.constants.CommandLineArguments.NO_INPUT_FILE_COMMAND
import wiles.shared.constants.Settings.OBJECT_FILE
import java.io.FileWriter
import java.io.IOException


object Main {

    lateinit var finalCode : String
    var DEBUG = false
    lateinit var filename : String

    private fun printExceptions(exceptions : CompilationExceptionsCollection, input : String)
    {
        if (exceptions.size > 0)
            System.err.println("\u001B[31m"+exceptions.getExceptionsString(input)+"\u001B[0m")
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        //args
        DEBUG = args.contains(DEBUG_COMMAND)
        val noFile = args.contains(NO_INPUT_FILE_COMMAND)
        val writeCompileFile = args.contains(COMPILE_COMMAND)
        val exceptions = CompilationExceptionsCollection()

        filename = args.lastOrNull()?:throw InternalErrorException("Filename expected!")
        if(noFile)
            filename = "code.wiles"
        val parser = Parser(if(noFile) args.last() else null)
        exceptions.addAll(parser.getExceptions())
        val result = parser.getResults()

        if(DEBUG) {
            print("Syntax tree: ")
            println(result)
        }

        if(exceptions.isNotEmpty())
        {
            printExceptions(exceptions, parser.input)
            return
        }

        val checker = Checker(if(DEBUG) null else parser.json)
        exceptions.addAll(checker.check())

        if(DEBUG) {
            print("After checking: ")
            println(checker.code)
        }

        if(writeCompileFile && !noFile)
        {
            val writer = FileWriter(filename + OBJECT_FILE)
            writer.write(checker.codeAsJSONString)
            writer.close()
        }

        printExceptions(exceptions, parser.input)
        finalCode = checker.code.toString()

        if(!writeCompileFile)
        {
            val interpreter = Interpreter(checker.codeAsJSONString)
            interpreter.interpret()
        }
    }
}
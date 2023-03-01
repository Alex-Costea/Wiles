package wiles

import wiles.checker.Checker
import wiles.parser.Parser
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
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
        DEBUG = args.contains("-debug")
        val noFile = args.getOrNull(0)=="-nofile"
        val exceptions = CompilationExceptionsCollection()
        filename = args.getOrNull(0)?:throw InternalErrorException("Filename expected!")
        if(noFile)
            filename = "code.wiles"
        val parser = Parser(if(noFile) args[1] else null)
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

        printExceptions(exceptions, parser.input)
        finalCode = checker.code.toString()
    }
}
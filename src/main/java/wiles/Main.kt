package wiles

import wiles.checker.Checker
import wiles.parser.Parser
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import java.io.IOException

object Main {

    lateinit var finalCode : String
    var DEBUG = false

    private fun printExceptions(exceptions : CompilationExceptionsCollection, input : String)
    {
        if (exceptions.size > 0)
            System.err.println(exceptions.getExceptionsString(input))
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        DEBUG = args.contains("-debug")
        val exceptions = CompilationExceptionsCollection()
        val parser = Parser(args.getOrNull(0)?:throw InternalErrorException("Filename expected!"))
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
            printExceptions(exceptions, parser.input)
        }

        finalCode = checker.code.toString()

    }
}
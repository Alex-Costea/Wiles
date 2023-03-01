package wiles

import wiles.checker.Checker
import wiles.parser.Parser
import wiles.shared.CompilationExceptionsCollection
import java.io.IOException

object Main {

    lateinit var finalCode : String

    private fun printExceptions(exceptions : CompilationExceptionsCollection, input : String)
    {
        if (exceptions.size > 0)
            System.err.println(exceptions.getExceptionsString(input))
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val exceptions = CompilationExceptionsCollection()
        val parser = Parser(args[0])
        exceptions.addAll(parser.getExceptions())
        val result = parser.getResults()
        print("Syntax tree: ")
        println(result)

        if(exceptions.isNotEmpty())
        {
            printExceptions(exceptions, parser.input)
            return
        }

        val checker = Checker()
        exceptions.addAll(checker.check())
        printExceptions(exceptions,parser.input)
        print("After checking: ")
        finalCode = checker.code.toString()
        println(checker.code)
    }
}
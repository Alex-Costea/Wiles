package wiles

import wiles.checker.Checker
import wiles.parser.Parser
import wiles.shared.CompilationExceptionsCollection
import java.io.IOException

object Main {

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

        val checker = Checker()
        exceptions.addAll(checker.check())
        printExceptions(exceptions,parser.input)
        println(checker.code)
    }
}
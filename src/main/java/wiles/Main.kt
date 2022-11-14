package wiles

import wiles.checker.CheckerProcessor
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
        val parser = Parser("input.wiles")
        exceptions.addAll(parser.getExceptions())
        val result = parser.getResults()
        print("Syntax tree: ")
        println(result)
        exceptions.addAll(CheckerProcessor(result).exceptions)
        printExceptions(exceptions,parser.input)
    }

}
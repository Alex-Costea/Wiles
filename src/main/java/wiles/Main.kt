package wiles

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
        print("Syntax tree: ")
        println(parser.getResults())
        printExceptions(exceptions,parser.input)
        //val checker = Checker(result)
    }

}
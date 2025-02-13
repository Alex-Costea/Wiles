package wiles.interpreter

import wiles.shared.AbstractCompilationException
import wiles.shared.AbstractSyntaxTree
import java.util.*

class Interpreter(private val scanner : Scanner?,
                  private val syntax : AbstractSyntaxTree,
                  private val debug : Boolean) {
    val compileMode = scanner == null

    fun getOutput(): String {
        TODO("Not yet implemented")
    }

    fun getIdentifiers() : ValuesMap
    {
        TODO("Not yet implemented")
    }

    init{
        if(debug)
            println(syntax)
        TODO("Not yet implemented")
    }

    fun getExceptions(): Collection<AbstractCompilationException> {
        TODO("Not yet implemented")
    }
}
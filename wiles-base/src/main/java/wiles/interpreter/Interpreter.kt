package wiles.interpreter

import wiles.interpreter.interpreters.ProcessorProgram
import wiles.shared.AbstractCompilationException
import wiles.shared.AbstractSyntaxTree
import java.util.*

class Interpreter(scanner : Scanner?, syntax : AbstractSyntaxTree, debug : Boolean) {
    private val isRunning = scanner != null
    private val values = ValuesMap()

    fun getOutput(): String {
        TODO("Not yet implemented")
    }

    fun getValues() : ValuesMap
    {
        return values
    }

    init{
        if(isRunning)
        {
            val compiler = Interpreter(null, syntax, debug)
            values.putAll(compiler.getValues())
        }
        val context = InterpreterContext(isRunning, values, debug)
        val interpretFromProgram = ProcessorProgram(syntax, context)
        interpretFromProgram.process()
    }

    fun getExceptions(): Collection<AbstractCompilationException> {
        TODO("Not yet implemented")
    }
}
package wiles.interpreter

import wiles.interpreter.interpreters.ProcessorProgram
import wiles.shared.AbstractSyntaxTree
import wiles.shared.CompilationExceptionsCollection
import java.util.*

class Interpreter(scanner : Scanner?, syntax : AbstractSyntaxTree, debug : Boolean) {
    private val isRunning = scanner != null
    private val values = ValuesMap()

    fun getOutput(): String {
        //TODO
        return ""
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
        if(debug)
            println(context.values)
    }

    fun getExceptions(): CompilationExceptionsCollection {
        //TODO: implement
        return CompilationExceptionsCollection()
    }
}
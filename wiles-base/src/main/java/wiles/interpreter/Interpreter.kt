package wiles.interpreter

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.interpreters.ProcessorProgram
import wiles.interpreter.data.ValuesMap
import wiles.shared.AbstractSyntaxTree
import wiles.shared.WilesExceptionsCollection
import java.util.*

class Interpreter(scanner : Scanner?, syntax : AbstractSyntaxTree, debug : Boolean) {
    private val isRunning = scanner != null
    private val values = ValuesMap()
    private val exceptions = WilesExceptionsCollection()

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
        val context = InterpreterContext(isRunning, values, debug, exceptions)
        val interpretFromProgram = ProcessorProgram(syntax, context)
        interpretFromProgram.process()
        if(debug)
            println(context.values)
    }

    fun getExceptions(): WilesExceptionsCollection {
        return exceptions
    }
}
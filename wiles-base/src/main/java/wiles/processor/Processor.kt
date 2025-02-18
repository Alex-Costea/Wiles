package wiles.processor

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValuesMap
import wiles.processor.enums.KnownStatus
import wiles.processor.processors.ProcessorProgram
import wiles.processor.values.Value
import wiles.shared.AbstractSyntaxTree
import wiles.shared.WilesExceptionsCollection
import java.util.*

class Processor(scanner: Scanner?, val syntax: AbstractSyntaxTree, private val debug: Boolean) {
    private val isRunning: Boolean = scanner != null
    private val values: ValuesMap = ValuesMap()
    private val exceptions: WilesExceptionsCollection = WilesExceptionsCollection()

    private fun compile(syntax: AbstractSyntaxTree, debug: Boolean)
    {
        val compiler = Processor(null, syntax, debug)
        compiler.process()
        val compilerValues = compiler.values
        compilerValues.forEach { (key, value) ->
            val knownStatus = if(value.isVariable()) KnownStatus.Unknown else value.knownStatus()
            val obj = if(knownStatus == KnownStatus.Known) value.getObj() else null
            values[key] = Value(obj, value.getType(), value.variableStatus(), knownStatus)
        }
        if (compiler.getExceptions().size > 0) {
            exceptions.addAll(compiler.getExceptions())
            return
        }
    }

    fun process() {
        if (isRunning)
            compile(syntax, debug)
        val context = InterpreterContext(isRunning, values, debug, exceptions)
        val interpretFromProgram = ProcessorProgram(syntax, context)
        interpretFromProgram.process()
        if (debug) {
            print("After ${if (isRunning) "interpreting" else "compiling"}: ")
            println(context.values)
        }
    }

    fun getOutput(): String {
        //TODO
        return ""
    }

    fun getValues() : ValuesMap
    {
        return values
    }

    fun getExceptions(): WilesExceptionsCollection {
        return exceptions
    }
}
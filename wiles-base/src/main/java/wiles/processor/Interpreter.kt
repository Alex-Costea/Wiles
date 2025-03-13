package wiles.processor

import wiles.parser.Parser
import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.processors.ProcessorCodeBlock
import wiles.shared.abstracts.AbstractSyntaxTree
import wiles.shared.constants.StandardLibrary.STANDARD_LIBRARY_TEXT
import wiles.shared.constants.Utils.convertStatementToSyntaxTree
import wiles.shared.data.WilesExceptionsCollection
import java.util.*

class Interpreter(scanner: Scanner?, val syntax: AbstractSyntaxTree, private val isDebug: Boolean,
                  private val processingStandardLibrary : Boolean = false) {
    private val isRunning: Boolean = scanner != null
    private var values: ValuesMap = ValuesMap()
    private val exceptions: WilesExceptionsCollection = WilesExceptionsCollection()

    private fun compile(syntax: AbstractSyntaxTree, debug: Boolean) : Boolean
    {
        val compiler = Interpreter(null, syntax, debug)
        compiler.process()
        val newValues = ValuesMap()
        for((name, value) in compiler.values)
        {
            if(!value.isKnown())
                newValues[name] = Value(if(value.isVariable()) VariableStatus.Var else VariableStatus.Const,
                    null, value.getComptimeType())
            else if(value.isVariable())
                newValues[name] = Value(VariableStatus.Var, null, value.getComptimeType())
            else newValues[name] = value
        }
        values = compiler.values
        values.clear()
        values.putAll(newValues)
        if (compiler.getExceptions().size > 0) {
            exceptions.addAll(compiler.getExceptions())
            return true
        }
        return false
    }

    fun process() {
        if (isRunning)
            if(compile(syntax, isDebug))
                return
        if(!processingStandardLibrary && !isRunning)
        {
            values.putAll(standardLibrary)
        }
        val context = InterpreterContext(values, isRunning, isDebug, exceptions)
        val interpretFromProgram = ProcessorCodeBlock(syntax, context)
        interpretFromProgram.process()
        if (isDebug) {
            print("After ${if (isRunning) "interpreting" else "compiling"}: ")
            println(getValuesExceptStandard())
        }
        val distinctExceptions = exceptions.distinct()
        exceptions.clear()
        exceptions.addAll(distinctExceptions)
    }

    private fun getValuesExceptStandard(): Map<String, Value> {
        return values.filter {!standardLibrary.containsKey(it.key)}
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

    companion object{
        private val standardLibrary = kotlin.run {
            val parser = Parser(STANDARD_LIBRARY_TEXT, false)
            val syntax = convertStatementToSyntaxTree(parser.getResults())
            val interpreter = Interpreter(null, syntax, isDebug = false, processingStandardLibrary = true)
            interpreter.process()
            return@run interpreter.getValues()
        }
    }
}

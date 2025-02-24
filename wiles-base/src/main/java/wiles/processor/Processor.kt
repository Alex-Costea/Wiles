package wiles.processor

import wiles.parser.Parser
import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.processors.ProcessorProgram
import wiles.shared.AbstractSyntaxTree
import wiles.shared.WilesExceptionsCollection
import wiles.shared.constants.StandardLibrary.STANDARD_LIBRARY_TEXT
import wiles.shared.constants.Utils.convertStatementToSyntaxTree
import java.util.*

class Processor(scanner: Scanner?, val syntax: AbstractSyntaxTree, private val debug: Boolean,
                private val processingStandardLibrary : Boolean = false) {
    private val isRunning: Boolean = scanner != null
    private val values: ValuesMap = ValuesMap()
    private lateinit var standardLibraryNames : Set<String>
    private val exceptions: WilesExceptionsCollection = WilesExceptionsCollection()

    private fun getStandardLibrary() : ValuesMap
    {
        val parser = Parser(STANDARD_LIBRARY_TEXT, false)
        val syntax = convertStatementToSyntaxTree(parser.getResults())
        val processor = Processor(null, syntax, debug = false, processingStandardLibrary = true)
        processor.process()
        return processor.getValues()
    }

    private fun compile(syntax: AbstractSyntaxTree, debug: Boolean) : Boolean
    {
        val compiler = Processor(null, syntax, debug)
        compiler.process()
        values.putAll(compiler.values.filter{ it.value.isKnown() && !it.value.isVariable()})
        this.standardLibraryNames = compiler.standardLibraryNames
        if (compiler.getExceptions().size > 0) {
            exceptions.addAll(compiler.getExceptions())
            return true
        }
        return false
    }

    fun process() {
        if (isRunning)
            if(compile(syntax, debug))
                return
        if(!processingStandardLibrary && !isRunning)
        {
            val standardLibraryValues = getStandardLibrary()
            standardLibraryNames = standardLibraryValues.keys
            values.putAll(standardLibraryValues)
        }
        val context = InterpreterContext(isRunning, values, debug, exceptions)
        val interpretFromProgram = ProcessorProgram(syntax, context)
        interpretFromProgram.process()
        if (debug) {
            print("After ${if (isRunning) "interpreting" else "compiling"}: ")
            println(getValuesExceptStandard())
        }
    }

    private fun getValuesExceptStandard(): Map<String, Value> {
        return values.filter { it.key !in standardLibraryNames }
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
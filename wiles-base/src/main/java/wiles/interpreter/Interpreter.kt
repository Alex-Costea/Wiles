package wiles.interpreter

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.ValuesMap
import wiles.interpreter.interpreters.ProcessorProgram
import wiles.shared.AbstractSyntaxTree
import wiles.shared.WilesExceptionsCollection
import java.util.*

@Suppress("ConvertSecondaryConstructorToPrimary")
class Interpreter {
    private val isRunning: Boolean
    private val values: ValuesMap
    private val exceptions: WilesExceptionsCollection

    constructor(scanner: Scanner?, syntax: AbstractSyntaxTree, debug: Boolean) {
        this.isRunning = scanner != null
        this.values = ValuesMap()
        this.exceptions = WilesExceptionsCollection()
        if (isRunning) {
            val compiler = Interpreter(null, syntax, debug)
            //TODO: figure out how to use values
            //values.putAll(compiler.getValues())
            if (compiler.getExceptions().size > 0) {
                exceptions.addAll(compiler.getExceptions())
                return
            }
        }
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
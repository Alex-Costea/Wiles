package wiles.interpreter

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.PanicException
import wiles.interpreter.services.InterpreterService
import wiles.shared.JSONService
import wiles.shared.JSONStatement
import wiles.shared.constants.ErrorMessages.STACK_OVERFLOW_ERROR
import wiles.shared.constants.StandardLibrary.defaultInterpreterVars
import java.io.File

class Interpreter(private val code : String?, private val debug : Boolean, private val filename : String, val context: InterpreterContext) {
    private val input = parseJson()
    var newVars = InterpreterVariableMap()

    private fun parseJson(): JSONStatement {
        if(code==null)
            return JSONService.readValueAsJSONStatement(File(filename))
        return JSONService.readValueAsJSONStatement(code)
    }

    fun interpret()
    {
        if(debug) {
            print("Interpreting code: ")
            println(input)
        }

        val variableMap = InterpreterVariableMap()
        variableMap.putAll(defaultInterpreterVars)
        try
        {
            InterpreterService(input, variableMap, context).interpret()
        }
        catch (ex : StackOverflowError)
        {
            throw PanicException(STACK_OVERFLOW_ERROR)
        }

        if(debug)
        {
            println()
            print("Variables: ")
            newVars.putAll(variableMap.filter{!defaultInterpreterVars.containsKey(it.key)})
            println(newVars.display())
        }
    }
}
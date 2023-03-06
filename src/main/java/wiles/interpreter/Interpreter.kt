package wiles.interpreter

import com.fasterxml.jackson.databind.ObjectMapper
import wiles.Main
import wiles.Main.DEBUG
import wiles.interpreter.data.VariableMap
import wiles.interpreter.exceptions.PanicException
import wiles.interpreter.services.InterpretFrom
import wiles.interpreter.statics.InterpreterConstants.defaultVariableMap
import wiles.shared.JSONStatement
import java.io.File

class Interpreter(private val code : String?) {
    private val input = parseJson()
    var newVars = VariableMap()

    private fun parseJson(): JSONStatement {
        if(code==null)
            return ObjectMapper().readValue(File(Main.filename), JSONStatement::class.java)
        return ObjectMapper().readValue(code, JSONStatement::class.java)
    }

    fun interpret()
    {
        if(DEBUG) {
            print("Interpreting code: ")
            println(input)
        }

        val variableMap = VariableMap()
        variableMap.putAll(defaultVariableMap)
        try {
            InterpretFrom(input, variableMap, VariableMap()).interpret()
        }
        catch (ex : PanicException)
        {
            System.err.println("\u001B[31m${ex.message?:"A runtime error occurred!"}\u001B[0m")
        }

        if(DEBUG)
        {
            print("Variables: ")
            newVars.putAll(variableMap.filter{it.key !in defaultVariableMap})
            println(newVars)
        }
    }
}
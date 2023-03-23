package wiles.interpreter

import com.fasterxml.jackson.databind.ObjectMapper
import wiles.Main
import wiles.Main.DEBUG
import wiles.interpreter.data.InterpreterVariableMap
import wiles.interpreter.exceptions.PanicException
import wiles.interpreter.services.InterpreterService
import wiles.shared.JSONStatement
import wiles.shared.constants.ErrorMessages.RED_TEXT_END_ERROR
import wiles.shared.constants.ErrorMessages.RED_TEXT_START_ERROR
import wiles.shared.constants.ErrorMessages.STACK_OVERFLOW_ERROR
import wiles.shared.constants.StandardLibrary.defaultInterpreterVars
import java.io.File

class Interpreter(private val code : String?) {
    private val input = parseJson()
    var newVars = InterpreterVariableMap()

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

        val variableMap = InterpreterVariableMap()
        variableMap.putAll(defaultInterpreterVars)
        try {
            try
            {
                InterpreterService(input, variableMap, InterpreterVariableMap()).interpret()
            }
            catch (ex : StackOverflowError)
            {
                throw PanicException(STACK_OVERFLOW_ERROR)
            }
        }
        catch (ex : PanicException)
        {
            System.err.println("$RED_TEXT_START_ERROR${ex.message?:"A runtime error occurred!"}$RED_TEXT_END_ERROR")
        }

        if(DEBUG)
        {
            println()
            print("Variables: ")
            newVars.putAll(variableMap.filter{it.key !in defaultInterpreterVars})
            println(newVars.map { it.key + " -> " + it.value})
        }
    }
}
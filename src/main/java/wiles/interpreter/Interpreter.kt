package wiles.interpreter

import com.fasterxml.jackson.databind.ObjectMapper
import wiles.Main
import wiles.Main.DEBUG
import wiles.interpreter.InterpreterConstants.defaultVariableMap
import wiles.shared.JSONStatement
import java.io.File

class Interpreter(private val code : String?) {
    private val input = parseJson()

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

        InterpretFrom(input, defaultVariableMap, VariableMap()).interpret()
    }
}
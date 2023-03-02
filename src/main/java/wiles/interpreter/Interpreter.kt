package wiles.interpreter

import com.fasterxml.jackson.databind.ObjectMapper
import wiles.Main.DEBUG
import wiles.shared.JSONStatement
import wiles.shared.constants.Settings
import java.io.File

class Interpreter(private val code : String?) {
    private val input = parseJson()

    private fun parseJson(): JSONStatement {
        if(code==null)
            return ObjectMapper().readValue(File(Settings.SYNTAX_TREE_FILE), JSONStatement::class.java)
        return ObjectMapper().readValue(code, JSONStatement::class.java)
    }

    fun interpret()
    {
        if(DEBUG) {
            print("Interpreting code: ")
            println(input)
        }

        InterpreterService(input).run()
    }
}
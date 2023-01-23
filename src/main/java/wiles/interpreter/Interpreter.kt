package wiles.interpreter

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class Interpreter
{
    var codeText : String? = null
    private fun jsonParse(): JSONStatement {
        val mapper = ObjectMapper()
        return mapper.readValue(File("syntaxTree.json"), JSONStatement::class.java)
    }

    fun interpret()
    {
        val code = jsonParse()
        codeText = code.toString()
        //TODO: check types/initializations and then interpret
    }
}
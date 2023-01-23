package wiles.interpreter

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object Interpreter
{
    private fun jsonParse(): JSONStatement {
        val mapper = ObjectMapper()
        return mapper.readValue(File("syntaxTree.json"), JSONStatement::class.java)
    }

    fun interpret()
    {
        val code = jsonParse()
        print(code)
        //TODO: check types/initializations and then interpret
    }
}
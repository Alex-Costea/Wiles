package wiles.checker

import com.fasterxml.jackson.databind.ObjectMapper
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.constants.Settings
import java.io.File

class Checker {
    val code = parseSyntaxTreeJson()

    private fun parseSyntaxTreeJson(): JSONStatement {
        val mapper = ObjectMapper()
        return mapper.readValue(File(Settings.SYNTAX_TREE_FILE), JSONStatement::class.java)
    }
    fun check() : CompilationExceptionsCollection
    {
        val inferrer = Inferrer(code, hashMapOf())
        try
        {
            inferrer.infer()
        }
        catch (ex : NotImplementedError)
        {
            ex.printStackTrace()
        }
        return inferrer.exceptions
    }
}
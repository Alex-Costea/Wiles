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
        val exceptions = CompilationExceptionsCollection()
        //TODO: check correct types when specified
        //      check correct declarations/initializations
        //      check top level definitions are of type nothing
        //      add inferred type definitions and return types
        //      convert operations e.g. STRING + INT64 -> CONCAT_STRING_INT64
        return exceptions
    }
}
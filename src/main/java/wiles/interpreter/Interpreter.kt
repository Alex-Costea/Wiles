package wiles.interpreter

import com.fasterxml.jackson.databind.ObjectMapper
import wiles.interpreter.checker.Checker
import wiles.shared.CompilationExceptionsCollection
import java.io.File

class Interpreter
{
    var codeText : String? = null
    private fun jsonParse(): JSONStatement {
        val mapper = ObjectMapper()
        return mapper.readValue(File("syntaxTree.json"), JSONStatement::class.java)
    }

    fun interpret() : CompilationExceptionsCollection
    {
        val code = jsonParse()
        codeText = code.toString()
        val checker = Checker(code)
        val exceptions = checker.check()
        if(exceptions.isNotEmpty())
            return exceptions

        //TODO: interpret

        return CompilationExceptionsCollection()
    }
}
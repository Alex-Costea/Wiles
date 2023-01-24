package wiles.checker

import com.fasterxml.jackson.databind.ObjectMapper
import wiles.checker.InferrerUtils.BOOLEAN_TYPE
import wiles.checker.InferrerUtils.NOTHING_TYPE
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.constants.Settings
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
import java.io.File

class Checker {
    val code = parseSyntaxTreeJson()
    private val variables = hashMapOf(
            Pair(TRUE_ID, VariableDetails(BOOLEAN_TYPE)),
            Pair(FALSE_ID, VariableDetails(BOOLEAN_TYPE)),
            Pair(NOTHING_ID, VariableDetails(NOTHING_TYPE)),
        )

    private fun parseSyntaxTreeJson(): JSONStatement {
        val mapper = ObjectMapper()
        return mapper.readValue(File(Settings.SYNTAX_TREE_FILE), JSONStatement::class.java)
    }
    fun check() : CompilationExceptionsCollection
    {
        val inferrer = Inferrer(code, variables)
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
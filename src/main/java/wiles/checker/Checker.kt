package wiles.checker

import com.fasterxml.jackson.databind.ObjectMapper
import wiles.checker.CheckerConstants.BOOLEAN_TYPE
import wiles.checker.CheckerConstants.NOTHING_TYPE
import wiles.checker.CheckerConstants.WRITELINE_TYPE
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.constants.Settings
import wiles.shared.constants.Tokens.FALSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TRUE_ID
import java.io.File

class Checker(private val jsonCode : String? = null) {
    val code: JSONStatement = parseSyntaxTreeJson()

    private val variables = VariableMap(hashMapOf(
            Pair(TRUE_ID, VariableDetails(BOOLEAN_TYPE)),
            Pair(FALSE_ID, VariableDetails(BOOLEAN_TYPE)),
            Pair(NOTHING_ID, VariableDetails(NOTHING_TYPE)),
            Pair("!write", VariableDetails(WRITELINE_TYPE)),
            Pair("!writeline", VariableDetails(WRITELINE_TYPE))
        ))

    private fun parseSyntaxTreeJson(): JSONStatement {
        if(jsonCode==null)
            return ObjectMapper().readValue(File(Settings.SYNTAX_TREE_FILE), JSONStatement::class.java)
        return ObjectMapper().readValue(jsonCode, JSONStatement::class.java)
    }

    fun check() : CompilationExceptionsCollection
    {
        val inferrer = Inferrer(InferrerDetails(code, variables, CompilationExceptionsCollection()))
        variables.putAll(CompatibleAccess.getVariables())
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

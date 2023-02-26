package wiles.checker

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.data.VariableMap
import wiles.checker.services.AccessOperationIdentifiers
import wiles.checker.services.Inferrer
import wiles.checker.statics.CheckerConstants
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Settings
import wiles.shared.constants.Tokens
import java.io.File

class Checker(private val jsonCode : String? = null) {
    val code: JSONStatement = parseSyntaxTreeJson()

    private fun parseSyntaxTreeJson(): JSONStatement {
        if(jsonCode==null)
            return ObjectMapper().readValue(File(Settings.SYNTAX_TREE_FILE), JSONStatement::class.java)
        return ObjectMapper().readValue(jsonCode, JSONStatement::class.java)
    }

    private fun removeTypes(statement : JSONStatement) : JSONStatement
    {
        if(statement.components.isNotEmpty() && statement.components[0].type == SyntaxType.TYPE
            && ! (statement.type == SyntaxType.EXPRESSION && statement.components.size == 2)
            && statement.type != SyntaxType.METHOD_CALL)
            statement.components.removeFirst()

        for(component in statement.components)
        {
            removeTypes(component)
        }
        return statement
    }

    private fun writeObjectFile()
    {
        val mapper = JsonMapper.builder().disable(MapperFeature.AUTO_DETECT_CREATORS).disable(MapperFeature.AUTO_DETECT_FIELDS)
            .disable(MapperFeature.AUTO_DETECT_GETTERS).disable(MapperFeature.AUTO_DETECT_IS_GETTERS).build()

        val writer = mapper.writer(DefaultPrettyPrinter())
        writer.writeValue(File(Settings.OBJECT_FILE), removeTypes(code.copyRemovingLocation()))
    }

    fun check() : CompilationExceptionsCollection
    {
        val inferrer = Inferrer(InferrerDetails(code, getVariables(), CompilationExceptionsCollection(), VariableMap()))
        try
        {
            inferrer.infer()
        }
        catch (ex : NotImplementedError)
        {
            ex.printStackTrace()
        }
        writeObjectFile()
        return inferrer.exceptions
    }

    companion object {
        fun getVariables(): VariableMap {
            val vars = VariableMap(
                hashMapOf(
                    Pair(Tokens.TRUE_ID, VariableDetails(CheckerConstants.BOOLEAN_TYPE)),
                    Pair(Tokens.FALSE_ID, VariableDetails(CheckerConstants.BOOLEAN_TYPE)),
                    Pair(Tokens.NOTHING_ID, VariableDetails(CheckerConstants.NOTHING_TYPE)),
                    Pair("!write", VariableDetails(CheckerConstants.WRITELINE_TYPE)),
                    Pair("!writeline", VariableDetails(CheckerConstants.WRITELINE_TYPE))
                )
            ).copy()
            vars.putAll(AccessOperationIdentifiers.getVariables())
            return vars
        }
    }
}

package wiles.checker

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import wiles.checker.statics.CheckerConstants.BOOLEAN_TYPE
import wiles.checker.statics.CheckerConstants.NOTHING_TYPE
import wiles.checker.statics.CheckerConstants.WRITELINE_TYPE
import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.data.VariableMap
import wiles.checker.services.AccessOperationIdentifiers
import wiles.checker.services.Inferrer
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
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
        val inferrer = Inferrer(InferrerDetails(code, variables, CompilationExceptionsCollection()))
        variables.putAll(AccessOperationIdentifiers.getVariables())
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
}

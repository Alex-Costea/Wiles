package wiles.checker

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.data.VariableMap
import wiles.checker.services.AccessOperationIdentifiers
import wiles.checker.services.InferrerService
import wiles.checker.statics.CheckerConstants
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Settings
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.NOTHING_ID
import java.io.File

class Checker(private val jsonCode : String? = null) {
    val code: JSONStatement = parseSyntaxTreeJson()
    private val inferrer = InferrerService(InferrerDetails(code, getVariables(), CompilationExceptionsCollection(), VariableMap()))


    private fun parseSyntaxTreeJson(): JSONStatement {
        if(jsonCode==null)
            return ObjectMapper().readValue(File(Settings.SYNTAX_TREE_FILE), JSONStatement::class.java)
        return ObjectMapper().readValue(jsonCode, JSONStatement::class.java)
    }

    private fun createObject(statement : JSONStatement, topLevel : Boolean = true) : JSONStatement
    {
        if(statement.components.isNotEmpty() && statement.components[0].type == SyntaxType.TYPE
            && ! (statement.type == SyntaxType.EXPRESSION && statement.components.size == 2)
            && statement.type != SyntaxType.METHOD_CALL)
            statement.components.removeFirst()

        if(statement.type == SyntaxType.EXPRESSION && statement.components.size == 2)
        {
            statement.components.add(0,NOTHING_TOKEN)
        }

        for(component in statement.components)
        {
            createObject(component,false)
        }
        if(topLevel)
            statement.parsed = inferrer.exceptions.isEmpty()
        return statement
    }

    private fun writeObjectFile()
    {
        val mapper = JsonMapper.builder().disable(MapperFeature.AUTO_DETECT_CREATORS).disable(MapperFeature.AUTO_DETECT_FIELDS)
            .disable(MapperFeature.AUTO_DETECT_GETTERS).disable(MapperFeature.AUTO_DETECT_IS_GETTERS).build()

        val writer = mapper.writer(DefaultPrettyPrinter())
        writer.writeValue(File(Settings.OBJECT_FILE), createObject(code.copyRemovingLocation()))
    }

    fun check() : CompilationExceptionsCollection
    {
        inferrer.infer()
        writeObjectFile()
        return inferrer.exceptions
    }

    companion object {
        fun getVariables(): VariableMap {
            val vars = VariableMap(
                hashMapOf(
                    Pair(Tokens.TRUE_ID, VariableDetails(CheckerConstants.BOOLEAN_TYPE)),
                    Pair(Tokens.FALSE_ID, VariableDetails(CheckerConstants.BOOLEAN_TYPE)),
                    Pair(NOTHING_ID, VariableDetails(CheckerConstants.NOTHING_TYPE)),
                    Pair("!write", VariableDetails(CheckerConstants.WRITELINE_TYPE)),
                    Pair("!writeline", VariableDetails(CheckerConstants.WRITELINE_TYPE)),
                    Pair("!ignore", VariableDetails(CheckerConstants.IGNORE_TYPE)),
                    Pair("!modulo", VariableDetails(CheckerConstants.MODULO_TYPE)),
                )
            ).copy()
            vars.putAll(AccessOperationIdentifiers.getVariables())
            return vars
        }
        private val NOTHING_TOKEN = JSONStatement(name = NOTHING_ID, type = SyntaxType.TOKEN)
    }

}

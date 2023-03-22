package wiles.checker

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import wiles.checker.data.CheckerVariableMap
import wiles.checker.data.InferrerDetails
import wiles.checker.services.InferrerService
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Settings
import wiles.shared.constants.StandardLibrary.defaultCheckerVars
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import java.io.File
import java.util.*

class Checker(private val jsonCode : String? = null) {
    val code: JSONStatement = parseSyntaxTreeJson()
    private val inferrer = InferrerService(InferrerDetails(code,
        defaultCheckerVars.copy(),
        CompilationExceptionsCollection(),
        CheckerVariableMap()))
    lateinit var codeAsJSONString : String

    private fun parseSyntaxTreeJson(): JSONStatement {
        if(jsonCode==null)
            return ObjectMapper().readValue(File(Settings.SYNTAX_TREE_FILE), JSONStatement::class.java)
        return ObjectMapper().readValue(jsonCode, JSONStatement::class.java)
    }

    private fun createObject(statement : JSONStatement, topLevel : Boolean = true) : JSONStatement
    {
        if(statement.components.isNotEmpty() && statement.components[0].type == SyntaxType.TYPE
                //type is necessary when declaring new variables
                && ((statement.type !in KEEP_TYPE)
                //remove else type details
                || (statement.type == SyntaxType.TYPE && statement.name == ELSE_ID)))
            statement.components.removeFirst()

        if(statement.type == SyntaxType.EXPRESSION
            && statement.components[0].type != SyntaxType.TYPE
            && statement.components.size == 2)
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
        codeAsJSONString = writer.writeValueAsString(createObject(code.copyRemovingLocation()))
    }

    fun check() : CompilationExceptionsCollection
    {
        inferrer.infer()
        writeObjectFile()
        return inferrer.exceptions
    }

    companion object {
        private val NOTHING_TOKEN = JSONStatement(name = NOTHING_ID, type = SyntaxType.TOKEN)
        val scanner = Scanner(System.`in`)
        var currentFunctionNumber : Long = 0
        val KEEP_TYPE =
            arrayOf(SyntaxType.FOR, SyntaxType.DECLARATION, SyntaxType.TYPE, SyntaxType.LIST, SyntaxType.METHOD)
    }

}

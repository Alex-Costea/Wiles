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
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Settings
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.TypeConstants
import java.io.File
import java.util.*

class Checker(private val jsonCode : String? = null) {
    val code: JSONStatement = parseSyntaxTreeJson()
    private val inferrer = InferrerService(InferrerDetails(code, getVariables(), CompilationExceptionsCollection(), VariableMap()))
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
            && statement.type !in arrayOf(SyntaxType.FOR,SyntaxType.DECLARATION, SyntaxType.TYPE,
                SyntaxType.LIST, SyntaxType.METHOD))
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
        fun getVariables(): VariableMap {
            val vars = VariableMap(
                hashMapOf(
                    Pair(Tokens.TRUE_ID, VariableDetails(TypeConstants.BOOLEAN_TYPE)),
                    Pair(Tokens.FALSE_ID, VariableDetails(TypeConstants.BOOLEAN_TYPE)),
                    Pair(NOTHING_ID, VariableDetails(TypeConstants.NOTHING_TYPE)),
                    Pair("!write", VariableDetails(TypeConstants.WRITELINE_TYPE)),
                    Pair("!writeline", VariableDetails(TypeConstants.WRITELINE_TYPE)),
                    Pair("!panic", VariableDetails(TypeConstants.PANIC_TYPE)),
                    Pair("!ignore", VariableDetails(TypeConstants.IGNORE_TYPE)),
                    Pair("!modulo", VariableDetails(TypeConstants.MODULO_TYPE)),
                    Pair("!read_int", VariableDetails(TypeConstants.READ_NOTHING_RETURN_INT_TYPE)),
                    Pair("!read_line", VariableDetails(TypeConstants.READ_NOTHING_RETURN_STRING_TYPE)),
                    Pair("!read_rational", VariableDetails(TypeConstants.READ_NOTHING_RETURN_DOUBLE_TYPE)),
                    Pair("!read_truth", VariableDetails(TypeConstants.READ_NOTHING_RETURN_BOOL_TYPE)),
                )
            ).copy()
            vars.putAll(AccessOperationIdentifiers.getVariables())
            return vars
        }
        private val NOTHING_TOKEN = JSONStatement(name = NOTHING_ID, type = SyntaxType.TOKEN)
        val scanner = Scanner(System.`in`)
    }

}

package wiles.checker

import wiles.checker.data.InferrerDetails
import wiles.checker.services.InferrerService
import wiles.shared.*
import wiles.shared.constants.Settings
import wiles.shared.constants.StandardLibrary.defaultCheckerVars
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import java.io.File

class Checker(private val jsonCode : String? = null) {
    val code: JSONStatement = parseSyntaxTreeJson()
    private val inferrer = InferrerService(InferrerDetails(code,
        defaultCheckerVars.copy(),
        CompilationExceptionsCollection()))
    lateinit var codeAsJSONString : String

    private fun parseSyntaxTreeJson(): JSONStatement {
        if(jsonCode==null)
            return JSONService.readValueAsJSONStatement(File(Settings.SYNTAX_TREE_FILE))
        return JSONService.readValueAsJSONStatement(jsonCode)
    }

    private fun createObject(statement : JSONStatement, topLevel : Boolean = true) : JSONStatement
    {
        if(statement.components.isNotEmpty() && statement.components[0].syntaxType == SyntaxType.TYPE
                //type is necessary when declaring new variables
                && ((statement.syntaxType !in KEEP_TYPE)
                //remove else type details
                || (statement.syntaxType == SyntaxType.TYPE && statement.name == ELSE_ID)))
            statement.components.removeFirst()

        if(statement.syntaxType == SyntaxType.EXPRESSION
            && statement.components[0].syntaxType != SyntaxType.TYPE
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
        codeAsJSONString = JSONService.writeValueAsString(createObject(code.copyRemovingLocation()))
    }

    fun check() : CompilationExceptionsCollection
    {
        try
        {
            inferrer.infer()
        }
        catch(ex : AbstractCompilationException)
        {
            inferrer.exceptions.add(ex)
        }
        writeObjectFile()
        return inferrer.exceptions
    }

    companion object {
        private val NOTHING_TOKEN = JSONStatement(name = NOTHING_ID, syntaxType = SyntaxType.TOKEN)
        val KEEP_TYPE = arrayOf(SyntaxType.FOR, SyntaxType.DECLARATION, SyntaxType.TYPE, SyntaxType.LIST,
                SyntaxType.DICT, SyntaxType.METHOD)
    }

}

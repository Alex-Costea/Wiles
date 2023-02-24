package wiles.checker

import wiles.checker.CheckerConstants.ANYTHING_TYPE
import wiles.checker.CheckerConstants.INT64_TYPE
import wiles.checker.CheckerConstants.LIST_OF_ANYTHING_TYPE
import wiles.checker.CheckerConstants.NOTHING_TYPE
import wiles.checker.CheckerConstants.STRING_TYPE
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens

object AccessOperationIdentifiers {
    private val access : HashMap<String,List<Pair<JSONStatement,JSONStatement>>> = hashMapOf(
        Pair("!as_text", listOf(Pair(ANYTHING_TYPE, STRING_TYPE))),
        Pair("!size", listOf(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))),
        Pair("!write", listOf(Pair(STRING_TYPE, NOTHING_TYPE))),
        Pair("!writeline", listOf(Pair(STRING_TYPE, NOTHING_TYPE))),
    )

    private fun createFunctionType(variableType: Pair<JSONStatement, JSONStatement>): JSONStatement
    {
        return JSONStatement(type = SyntaxType.TYPE, name = Tokens.METHOD_ID,
            components = mutableListOf(
                JSONStatement(type = SyntaxType.METHOD,
                    components = mutableListOf(
                        variableType.second,
                        JSONStatement(type = SyntaxType.DECLARATION, name = Tokens.ANON_ARG_ID,
                            components = mutableListOf(variableType.first,
                                JSONStatement(type = SyntaxType.TOKEN, name = "!elem"))),
                    ))
            ))
    }

    fun getVariables() : List<Pair<String,VariableDetails>>
    {
        val list = mutableListOf<Pair<String,VariableDetails>>()
        for(key in access.keys)
        {
            for(variableType in access[key]!!)
            {
                list.add(Pair("!${variableType.first}$key", VariableDetails(createFunctionType(variableType))))
            }
        }
        return list
    }

    fun get(name: String, type: JSONStatement) : String?
    {
        //TODO: `set` for mutable values
        val list = access[name] ?: return null
        for(validType in list)
            if(InferrerUtils.isFormerSuperTypeOfLatter(validType.first,type))
                return "!${validType.first}$name"
        return null
    }
}
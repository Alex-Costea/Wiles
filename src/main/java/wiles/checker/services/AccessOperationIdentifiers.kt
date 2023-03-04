package wiles.checker.services

import wiles.checker.data.VariableDetails
import wiles.checker.statics.InferrerUtils
import wiles.shared.JSONStatement
import wiles.shared.constants.CheckerConstants.ANYTHING_TYPE
import wiles.shared.constants.CheckerConstants.INT64_TYPE
import wiles.shared.constants.CheckerConstants.LIST_OF_ANYTHING_TYPE
import wiles.shared.constants.CheckerConstants.STRING_TYPE
import wiles.shared.constants.Utils.createFunctionType

object AccessOperationIdentifiers {
    private val access : HashMap<String,List<Pair<JSONStatement,JSONStatement>>> = hashMapOf(
        Pair("!as_text", listOf(Pair(ANYTHING_TYPE, STRING_TYPE))),
        Pair("!size", listOf(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))),
    )

    fun getVariables() : List<Pair<String, VariableDetails>>
    {
        val list = mutableListOf<Pair<String, VariableDetails>>()
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
        val list = access[name] ?: return null
        for(validType in list)
            if(InferrerUtils.isFormerSuperTypeOfLatter(validType.first, type))
                return "!${validType.first}$name"
        return null
    }
}
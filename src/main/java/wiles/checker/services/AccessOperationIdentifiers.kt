package wiles.checker.services

import wiles.shared.JSONStatement
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.TypeConstants.LIST_OF_NULLABLE_ANYTHING_TYPE
import wiles.shared.constants.TypeConstants.STRING_TYPE
import wiles.shared.constants.TypeConstants.isFormerSuperTypeOfLatter

object AccessOperationIdentifiers {
    private val access : HashMap<String,List<Pair<JSONStatement,JSONStatement>>> = hashMapOf(
        Pair("!size", listOf(Pair(LIST_OF_NULLABLE_ANYTHING_TYPE, INT64_TYPE), Pair(STRING_TYPE, INT64_TYPE))),
    )

    fun get(name: String, type: JSONStatement) : String?
    {
        val list = access[name] ?: return null
        for(validType in list)
            if(isFormerSuperTypeOfLatter(validType.first, type))
                return "!${validType.first}$name"
        return null
    }
}
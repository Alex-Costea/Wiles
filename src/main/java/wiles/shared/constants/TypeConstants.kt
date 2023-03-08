package wiles.shared.constants

import wiles.checker.statics.InferrerUtils.makeNullable
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.METHOD_CALL_ID
import wiles.shared.constants.Types.STRING_ID

object TypeConstants {

    fun isFormerSuperTypeOfLatter(supertype : JSONStatement, subtype : JSONStatement) : Boolean
    {
        assert(supertype.type == SyntaxType.TYPE)
        assert(subtype.type == SyntaxType.TYPE)
        if(supertype.toString() == subtype.toString())
            return true

        if(supertype.name == ANYTHING_ID)
        {
            if(subtype.name != Types.EITHER_ID)
            {
                if (isFormerSuperTypeOfLatter(NOTHING_TYPE, subtype))
                    return false
                return true
            }
            else
            {
                for (component in subtype.components)
                {
                    if(isFormerSuperTypeOfLatter(NOTHING_TYPE,component))
                        return false
                    return true
                }
            }
        }

        else if(supertype.name == Types.EITHER_ID)
        {
            if(subtype.name != Types.EITHER_ID)
            {
                for (component in supertype.components)
                {
                    if (isFormerSuperTypeOfLatter(component,subtype))
                    {
                        return true
                    }
                }
            }
            else
            {
                for(subtypeComponent in subtype.components)
                {
                    if(isFormerSuperTypeOfLatter(supertype,subtypeComponent))
                    {
                        continue
                    }
                    var hasMatch = false
                    for(supertypeComponent in supertype.components)
                    {
                        if(isFormerSuperTypeOfLatter(supertypeComponent,subtypeComponent))
                        {
                            hasMatch = true
                            break
                        }
                    }
                    if(!hasMatch)
                        return false
                }
                return true
            }
        }

        else if(subtype.name == Types.EITHER_ID)
        {
            for(component in subtype.components)
            {
                if(!isFormerSuperTypeOfLatter(supertype, component)) {
                    return false
                }
            }
            return true
        }

        else if (supertype.name == Types.LIST_ID && subtype.name == Types.LIST_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0],subtype.components[0])

        else if (supertype.name == Tokens.MUTABLE_ID && subtype.name == Tokens.MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], subtype.components[0])

        else if (subtype.name == Tokens.MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype, subtype.components[0])

        else if(supertype.name == Tokens.METHOD_ID && subtype.name == Tokens.METHOD_ID)
            return checkMethodIsSubtype(supertype, subtype)

        return false
    }

    private fun checkMethodIsSubtype(supertype : JSONStatement, subtype: JSONStatement) : Boolean
    {
        val supertypeComponents = supertype.components[0].components.toMutableList()
        val subtypeComponents = subtype.components[0].components.toMutableList()

        val supertypeReturnType = if(supertypeComponents[0].type == SyntaxType.TYPE)
            supertypeComponents[0]
        else NOTHING_TYPE

        val subtypeReturnType = if(subtypeComponents[0].type == SyntaxType.TYPE)
            subtypeComponents[0]
        else NOTHING_TYPE

        if(!isFormerSuperTypeOfLatter(supertypeReturnType,subtypeReturnType))
            return false

        if(matchMethodComponentList(subtypeComponents,supertypeComponents,false) &&
            matchMethodComponentList(supertypeComponents,subtypeComponents,true)
            && checkUnnamedArgsInSameOrder(supertypeComponents,subtypeComponents))
            return true

        return false
    }

    private fun checkUnnamedArgsInSameOrder(list1: MutableList<JSONStatement>,
                                            list2: MutableList<JSONStatement>) : Boolean
    {
        while(list1.isNotEmpty() && list2.isNotEmpty())
        {
            val elem1 = list1[0]
            if(elem1.type == SyntaxType.TYPE || !elem1.name.contains(Tokens.ANON_ARG_ID)) {
                list1.removeFirst()
                continue
            }

            val elem2 = list2[0]
            if(elem2.type == SyntaxType.TYPE || !elem2.name.contains(Tokens.ANON_ARG_ID)) {
                list2.removeFirst()
                continue
            }

            if(elem1.components[1].name !=elem2.components[1].name)
                return false

            list1.removeFirst()
            list2.removeFirst()
        }

        while(list1.isNotEmpty()) {
            if (list1[0].type == SyntaxType.TYPE || !list1[0].name.contains(Tokens.ANON_ARG_ID))
                list1.removeFirst()
            else break
        }

        while(list2.isNotEmpty()) {
            if (list2[0].type == SyntaxType.TYPE || !list2[0].name.contains(Tokens.ANON_ARG_ID))
                list2.removeFirst()
            else break
        }

        if(list1.isNotEmpty() || list2.isNotEmpty())
            return false
        return true
    }

    private fun matchMethodComponentList(list1 : List<JSONStatement>, list2 : List<JSONStatement>,
                                         isSuperType : Boolean) : Boolean
    {
        for (component1 in list1) {
            if (component1.type == SyntaxType.TYPE)
                continue
            var matchFound = false
            for (component2 in list2) {
                if (component2.type == SyntaxType.TYPE)
                    continue

                val nameMatches = component1.components[1].name == component2.components[1].name
                if(nameMatches) {
                    val defaultValueMatches = !isSuperType || (component1.components.size <= component2.components.size)
                    if(defaultValueMatches) {
                        if(isSuperType) {
                            if (isFormerSuperTypeOfLatter(component1.components[0], component2.components[0]))
                                matchFound = true
                        }
                        else if(isFormerSuperTypeOfLatter(component2.components[0], component1.components[0]))
                            matchFound = true
                    }
                }
            }
            if (!matchFound)
                return false
        }
        return true
    }

    fun makeMutable(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = Tokens.MUTABLE_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeList(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = Types.LIST_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeMethod(type : JSONStatement) : JSONStatement
    {
        val newType = type.copyRemovingLocation()
        newType.components.removeLast()
        return JSONStatement(name = Tokens.METHOD_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(newType))
    }

    val NOTHING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = NOTHING_ID)
    val BOOLEAN_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.BOOLEAN_ID)
    val INT64_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.INT64_ID)
    val STRING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = STRING_ID)
    val DOUBLE_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Types.DOUBLE_ID)
    val ERROR_TYPE = JSONStatement(type = SyntaxType.TYPE, name = Tokens.ERROR_TOKEN)
    private val ANYTHING_TYPE = JSONStatement(type = SyntaxType.TYPE, name = ANYTHING_ID)
    val METHOD_CALL_TYPE = JSONStatement(type = SyntaxType.TYPE, name = METHOD_CALL_ID)
    val NULLABLE_ANYTHING_TYPE = makeNullable(ANYTHING_TYPE)

    val LIST_OF_ANYTHING_TYPE = makeList(ANYTHING_TYPE)

    val LIST_OF_NULLABLE_ANYTHING_TYPE = makeList(NULLABLE_ANYTHING_TYPE)

    val MUTABLE_ANYTHING_INCLUDING_NOTHING_TYPE = makeMutable(makeNullable(ANYTHING_TYPE))

    val NOTHING_TOKEN = JSONStatement(type = SyntaxType.TOKEN, name = NOTHING_ID)

    val PLUS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.PLUS_ID)
    val MINUS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.MINUS_ID)
    val TIMES_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.TIMES_ID)
    val DIVIDE_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.DIVIDE_ID)
    val POWER_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.POWER_ID)
    val UNARY_PLUS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.UNARY_PLUS_ID)
    val UNARY_MINUS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.UNARY_MINUS_ID)
    val AND_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.AND_ID)
    val OR_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.OR_ID)
    val NOT_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.NOT_ID)
    val EQUALS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.EQUALS_ID)
    val NOT_EQUAL_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.NOT_EQUAL_ID)
    val LARGER_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.LARGER_ID)
    val LARGER_EQUALS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.LARGER_EQUALS_ID)
    val SMALLER_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.SMALLER_ID)
    val SMALLER_EQUALS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.SMALLER_EQUALS_ID)
    val ACCESS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.ACCESS_ID)
    val ELEM_ACCESS_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.ELEM_ACCESS_ID)
    val APPLY_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.APPLY_ID)
    val ASSIGN_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.ASSIGN_ID)
    val MODIFY_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.MODIFY_ID)
    val MUTABLE_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.MUTABLE_ID)
    val IMPORT_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.IMPORT_ID)

    val WRITELINE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = STRING_ID, type = SyntaxType.TYPE),
                        JSONStatement(name = "!text", type = SyntaxType.TOKEN)
                    )
                ))
        ))
    )

    val PANIC_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = Types.EITHER_ID, type = SyntaxType.TYPE,
                            components = mutableListOf(
                                JSONStatement(name = STRING_ID, type = SyntaxType.TYPE),
                                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                            )),
                        JSONStatement(name = "!text", type = SyntaxType.TOKEN),
                        JSONStatement(type = SyntaxType.EXPRESSION, components = mutableListOf(
                            JSONStatement(name = NOTHING_ID, type =  SyntaxType.TYPE),
                            JSONStatement(name = NOTHING_ID, type =  SyntaxType.TOKEN),
                        )
                    )
                )
                )
            )
        ))
    )

    val IGNORE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        NULLABLE_ANYTHING_TYPE,
                        JSONStatement(name = "!elem", type = SyntaxType.TOKEN)
                    )
                ))
        ))
    )

    val MODULO_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                INT64_TYPE,
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!x", type = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = Tokens.ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!y", type = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )
}
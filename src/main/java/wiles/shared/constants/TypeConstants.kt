package wiles.shared.constants

import wiles.checker.data.GenericTypesMap
import wiles.checker.statics.InferrerUtils.makeGeneric
import wiles.checker.statics.InferrerUtils.makeNullable
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.STRING_START
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.GENERIC_ID
import wiles.shared.constants.Types.METHOD_CALL_ID
import wiles.shared.constants.Types.STRING_ID

object TypeConstants {

    fun isFormerSuperTypeOfLatter(
        supertype : JSONStatement, subtype : JSONStatement,
        unboxGenerics : Boolean = true, //should generics match?
        genericTypes : GenericTypesMap? = null,
        getMinus : Boolean = false,
    ): Boolean {
        assert(supertype.type == SyntaxType.TYPE)
        assert(subtype.type == SyntaxType.TYPE)

        if(supertype.toString() == subtype.toString()) {
            if(getMinus) {
                subtype.name = EITHER_ID
                subtype.components.clear()
            }
            return true
        }

        else if(supertype.name == GENERIC_ID && subtype.name == GENERIC_ID
            && supertype.components[0].name == subtype.components[0].name
            && isFormerSuperTypeOfLatter(supertype.components[1], subtype.components[1], getMinus = getMinus))
            return true

        if(supertype.name == GENERIC_ID && isFormerSuperTypeOfLatter(supertype.components[1], subtype,
                getMinus = getMinus && unboxGenerics)){
            val genName = supertype.components[0].name
            if(genericTypes?.containsKey(genName) == true)
            {
                return if(isFormerSuperTypeOfLatter(genericTypes[genName]!!.first, subtype, unboxGenerics = false)) {
                    genericTypes[genName] = Pair(genericTypes[genName]!!.first,true)
                    true
                }
                else if(isFormerSuperTypeOfLatter(subtype, genericTypes[genName]!!.first, unboxGenerics = false)) {
                    genericTypes[genName] = Pair(subtype,true)
                    true
                } else false
            }
            if (genericTypes!=null)
                genericTypes[genName] = Pair(subtype,false)
            if(unboxGenerics)
                return true
        }

        else if(subtype.name == GENERIC_ID)
        {
            return isFormerSuperTypeOfLatter(supertype,subtype.components[1], getMinus = getMinus)
        }

        else if(supertype.name == ANYTHING_ID)
        {
            return if(subtype.name != EITHER_ID) {
                !isFormerSuperTypeOfLatter(NOTHING_TYPE, subtype)
            } else {
                var isValid = true
                for (component in subtype.components) {
                    if(isFormerSuperTypeOfLatter(NOTHING_TYPE,component))
                        isValid = false
                }
                isValid
            }
        }

        else if(supertype.name == EITHER_ID)
        {
            if(subtype.name != EITHER_ID)
            {
                var isValid = false
                for (component in supertype.components)
                {
                    if (isFormerSuperTypeOfLatter(component,subtype, getMinus = getMinus))
                    {
                        isValid = true
                    }
                }
                return isValid
            }
            else
            {
                var isValid = true
                for(subtypeComponent in subtype.components)
                {
                    if(isFormerSuperTypeOfLatter(supertype,subtypeComponent, getMinus = getMinus))
                    {
                        continue
                    }
                    var hasMatch = false
                    for(supertypeComponent in supertype.components)
                    {
                        if(isFormerSuperTypeOfLatter(supertypeComponent,subtypeComponent, getMinus = getMinus))
                        {
                            hasMatch = true
                            break
                        }
                    }
                    if(!hasMatch)
                        isValid = false
                }
                return isValid
            }
        }

        else if(subtype.name == EITHER_ID)
        {
            var isValid = true
            for(component in subtype.components)
            {
                if(!isFormerSuperTypeOfLatter(supertype, component, getMinus = getMinus)) {
                    isValid = false
                }
            }
            return isValid
        }

        else if (supertype.name == Types.LIST_ID && subtype.name == Types.LIST_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0],subtype.components[0], getMinus = getMinus)

        else if (supertype.name == Tokens.MUTABLE_ID && subtype.name == Tokens.MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], subtype.components[0], getMinus = getMinus)

        else if (subtype.name == Tokens.MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype, subtype.components[0], getMinus = getMinus)

        else if(supertype.name == Tokens.METHOD_ID && subtype.name == Tokens.METHOD_ID)
            return checkMethodIsSubtype(supertype, subtype, genericTypes?: GenericTypesMap())

        return false
    }

    private fun checkMethodIsSubtype(
        supertype: JSONStatement, subtype: JSONStatement,
        genericTypes: GenericTypesMap) : Boolean
    {
        val supertypeComponents = supertype.components[0].components.toMutableList()
        val subtypeComponents = subtype.components[0].components.toMutableList()

        val supertypeReturnType = if(supertypeComponents[0].type == SyntaxType.TYPE)
            supertypeComponents[0]
        else NOTHING_TYPE

        val subtypeReturnType = if(subtypeComponents[0].type == SyntaxType.TYPE)
            subtypeComponents[0]
        else NOTHING_TYPE
        if(!isFormerSuperTypeOfLatter(supertypeReturnType, subtypeReturnType, genericTypes = genericTypes))
            return false

        if(matchMethodComponentList(subtypeComponents,supertypeComponents,false, genericTypes)
            && matchMethodComponentList(supertypeComponents,subtypeComponents,true, genericTypes)
            && checkUnnamedArgsInSameOrder(supertypeComponents, subtypeComponents)
            && checkValidReturnTypeForGenerics(supertypeReturnType, genericTypes)
        )
            return true

        return false
    }

    private fun checkValidReturnTypeForGenerics(
        supertypeReturnType: JSONStatement,
        genericTypes: GenericTypesMap
    ): Boolean {
        val genericComponents = getGenericComponents(supertypeReturnType)
        for(component in genericComponents)
        {
            assert(genericTypes.containsKey(component))
            val value = genericTypes[component]!!
            if(value.first.name != GENERIC_ID && value.second)
                return false
        }
        return true
    }

    private fun getGenericComponents(statement : JSONStatement) : List<String>
    {
        if(statement.name == GENERIC_ID)
            return listOf(statement.components[0].name)
        val list = mutableListOf<String>()
        for(component in statement.components)
        {
            list.addAll(getGenericComponents(statement))
        }
        return list
    }

    private fun checkUnnamedArgsInSameOrder(
        list1: MutableList<JSONStatement>,
        list2: MutableList<JSONStatement>
    ) : Boolean
    {
        while(list1.isNotEmpty() && list2.isNotEmpty())
        {
            val elem1 = list1[0]
            if(elem1.type == SyntaxType.TYPE || !elem1.name.contains(ANON_ARG_ID)) {
                list1.removeFirst()
                continue
            }

            val elem2 = list2[0]
            if(elem2.type == SyntaxType.TYPE || !elem2.name.contains(ANON_ARG_ID)) {
                list2.removeFirst()
                continue
            }

            if(elem1.components[1].name != elem2.components[1].name)
                return false

            list1.removeFirst()
            list2.removeFirst()
        }

        while(list1.isNotEmpty()) {
            if (list1[0].type == SyntaxType.TYPE || !list1[0].name.contains(ANON_ARG_ID))
                list1.removeFirst()
            else break
        }

        while(list2.isNotEmpty()) {
            if (list2[0].type == SyntaxType.TYPE || !list2[0].name.contains(ANON_ARG_ID))
                list2.removeFirst()
            else break
        }

        // allow subtype component with default value
        if(list1.isNotEmpty() || list2.any{it.components.size != 3})
            return false
        return true
    }

    private fun matchMethodComponentList(
        list1: List<JSONStatement>, list2: List<JSONStatement>,
        isSuperType: Boolean,
        genericTypes: GenericTypesMap?,
    ) : Boolean
    {
        for (component1 in list1) {
            if (component1.type == SyntaxType.TYPE
                // allow subtype component with default value
                || (!isSuperType && component1.components.size == 3))
                continue
            var matchFound = false
            for (component2 in list2) {
                if (component2.type == SyntaxType.TYPE)
                    continue

                val nameMatches = component1.components[1].name == component2.components[1].name
                if(nameMatches) {
                    val defaultValueMatches = !isSuperType || (component1.components.size <= component2.components.size)
                    if(defaultValueMatches) {
                        if(!isSuperType) {
                            if (isFormerSuperTypeOfLatter(component1.components[0], component2.components[0],
                                    genericTypes = genericTypes))
                                matchFound = true
                        }
                        else if(isFormerSuperTypeOfLatter(component2.components[0], component1.components[0],
                                genericTypes = genericTypes))
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
    private val NULLABLE_ANYTHING_TYPE = makeNullable(ANYTHING_TYPE)

    private val LIST_OF_ANYTHING_TYPE = makeList(ANYTHING_TYPE)

    val LIST_OF_NULLABLE_ANYTHING_TYPE = makeList(NULLABLE_ANYTHING_TYPE)

    val NOTHING_TOKEN = JSONStatement(type = SyntaxType.TOKEN, name = NOTHING_ID)

    val NULLABLE_STRING = makeNullable(STRING_TYPE)

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
    val MUTABLE_OPERATION = JSONStatement(type = SyntaxType.TOKEN, name = Tokens.MUTABLE_ID)

    val WRITE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = ANYTHING_ID, type = SyntaxType.TYPE),
                        JSONStatement(name = "!text", type = SyntaxType.TOKEN),
                    )
                ))
        ))
    )

    val WRITELINE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = ANYTHING_ID, type = SyntaxType.TYPE),
                        JSONStatement(name = "!text", type = SyntaxType.TOKEN),
                        JSONStatement(type = SyntaxType.EXPRESSION, components = mutableListOf(
                            JSONStatement(name = STRING_ID, type =  SyntaxType.TYPE),
                            JSONStatement(name = STRING_START, type =  SyntaxType.TOKEN),
                        ))
                    )
                ))
        ))
    )

    val PANIC_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(
                JSONStatement(name = NOTHING_ID, type = SyntaxType.TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE,
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
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
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
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!x", type = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        INT64_TYPE,
                        JSONStatement(name = "!y", type = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )

    val READ_NOTHING_RETURN_INT_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(INT64_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_STRING_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(STRING_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_BOOL_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(BOOLEAN_TYPE)
        ))
    )

    val READ_NOTHING_RETURN_DOUBLE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(DOUBLE_TYPE)
        ))
    )

    val LIST_OF_STRING = makeList(STRING_TYPE)

    private val SET_VALUE_GENERIC_TYPE = makeGeneric(makeNullable(ANYTHING_TYPE), "!T|set")
    val SET_VALUE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(NOTHING_TYPE,
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        makeMutable(SET_VALUE_GENERIC_TYPE),
                        JSONStatement(name = "!elem", type = SyntaxType.TOKEN)
                    )
                ),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        SET_VALUE_GENERIC_TYPE,
                        JSONStatement(name = "!value", type = SyntaxType.TOKEN)
                    )
                ),
                )
        ))
    )

    private val MAYBE_GENERIC_TYPE = makeGeneric(ANYTHING_TYPE, "!T|maybe")
    val MAYBE_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(makeNullable(MAYBE_GENERIC_TYPE),
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        MAYBE_GENERIC_TYPE,
                        JSONStatement(name = "!elem", type = SyntaxType.TOKEN)
                    )
                ),
            )
        ))
    )

    private val RUN_GENERIC_TYPE = makeGeneric(makeNullable(ANYTHING_TYPE), "!T|run")
    private val RUN_SUBTYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(RUN_GENERIC_TYPE)
        ))
    )
    val RUN_TYPE = JSONStatement(name = Tokens.METHOD_ID, type = SyntaxType.TYPE,
        components = mutableListOf(JSONStatement(type = SyntaxType.METHOD,
            components = mutableListOf(RUN_GENERIC_TYPE,
                JSONStatement(name = ANON_ARG_ID, type = SyntaxType.DECLARATION,
                    components = mutableListOf(
                        RUN_SUBTYPE,
                        JSONStatement(name = "!func", type = SyntaxType.TOKEN)
                    )
                )
            )
        ))
    )

    val AS_TEXT_TYPE = Utils.createFunctionType(Pair(NULLABLE_ANYTHING_TYPE, STRING_TYPE))
    val AS_LIST_TYPE = Utils.createFunctionType(Pair(STRING_TYPE, LIST_OF_STRING))
    val LIST_SIZE_TYPE = Utils.createFunctionType(Pair(LIST_OF_ANYTHING_TYPE, INT64_TYPE))
    val STRING_SIZE_TYPE = Utils.createFunctionType(Pair(STRING_TYPE, INT64_TYPE))
}
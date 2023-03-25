package wiles.shared.constants

import wiles.checker.data.GenericTypesMap
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

object TypeUtils {

    fun isFormerSuperTypeOfLatter(
        supertype : JSONStatement, subtype : JSONStatement,
        unboxGenerics : Boolean = true, //should generics match?
        genericTypes : GenericTypesMap? = null,
        getMinus : Boolean = false,
    ): Boolean {
        assert(supertype.syntaxType == SyntaxType.TYPE)
        assert(subtype.syntaxType == SyntaxType.TYPE)

        if(supertype.toString() == subtype.toString()) {
            if(getMinus) {
                subtype.name = Types.EITHER_ID
                subtype.components.clear()
            }
            return true
        }

        else if(supertype.name == Types.GENERIC_ID && subtype.name == Types.GENERIC_ID
            && supertype.components[0].name == subtype.components[0].name
            && isFormerSuperTypeOfLatter(supertype.components[1], subtype.components[1], genericTypes = genericTypes,
                getMinus = getMinus))
            return true

        if(supertype.name == Types.GENERIC_ID && isFormerSuperTypeOfLatter(supertype.components[1], subtype,
                getMinus = getMinus && unboxGenerics, genericTypes = genericTypes)){
            val genName = supertype.components[0].name
            if(genericTypes?.containsKey(genName) == true)
            {
                return if(isFormerSuperTypeOfLatter(genericTypes[genName]!!.first, subtype, unboxGenerics = false,
                        genericTypes = genericTypes)) {
                    genericTypes[genName] = Pair(genericTypes[genName]!!.first,true)
                    true
                }
                else if(isFormerSuperTypeOfLatter(subtype, genericTypes[genName]!!.first, unboxGenerics = false,
                        genericTypes = genericTypes)) {
                    genericTypes[genName] = Pair(subtype,true)
                    true
                } else false
            }
            if (genericTypes!=null)
                genericTypes[genName] = Pair(subtype,false)
            if(unboxGenerics)
                return true
        }

        else if(subtype.name == Types.GENERIC_ID)
        {
            return isFormerSuperTypeOfLatter(supertype,subtype.components[1], getMinus = getMinus,
                genericTypes = genericTypes)
        }

        else if(supertype.name == Types.ANYTHING_ID)
        {
            return if(subtype.name != Types.EITHER_ID) {
                !isFormerSuperTypeOfLatter(TypeConstants.NOTHING_TYPE, subtype, genericTypes = genericTypes)
            } else {
                var isValid = true
                for (component in subtype.components) {
                    if(isFormerSuperTypeOfLatter(TypeConstants.NOTHING_TYPE,component, genericTypes = genericTypes))
                        isValid = false
                }
                isValid
            }
        }

        else if(supertype.name == Types.EITHER_ID)
        {
            if(subtype.name != Types.EITHER_ID)
            {
                var isValid = false
                for (component in supertype.components)
                {
                    if (isFormerSuperTypeOfLatter(component,subtype, getMinus = getMinus, genericTypes = genericTypes))
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
                    if(isFormerSuperTypeOfLatter(supertype,subtypeComponent, getMinus = getMinus,
                            genericTypes = genericTypes))
                    {
                        continue
                    }
                    var hasMatch = false
                    for(supertypeComponent in supertype.components)
                    {
                        if(isFormerSuperTypeOfLatter(supertypeComponent,subtypeComponent, getMinus = getMinus,
                                genericTypes = genericTypes))
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

        else if(subtype.name == Types.EITHER_ID)
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
            return isFormerSuperTypeOfLatter(supertype.components[0],subtype.components[0], getMinus = getMinus, genericTypes = genericTypes)

        else if (supertype.name == Tokens.MUTABLE_ID && subtype.name == Tokens.MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], subtype.components[0], getMinus = getMinus,
                genericTypes = genericTypes)

        else if (subtype.name == Tokens.MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype, subtype.components[0], getMinus = getMinus,
                genericTypes = genericTypes)

        else if(supertype.name == Tokens.METHOD_ID && subtype.name == Tokens.METHOD_ID)
            return checkMethodIsSubtype(supertype, subtype, genericTypes?: GenericTypesMap())

        return false
    }

    private fun checkMethodIsSubtype(
        supertype: JSONStatement, subtype: JSONStatement,
        genericTypes: GenericTypesMap
    ) : Boolean
    {
        val supertypeComponents = supertype.components[0].components.toMutableList()
        val subtypeComponents = subtype.components[0].components.toMutableList()

        val supertypeReturnType = if(supertypeComponents[0].syntaxType == SyntaxType.TYPE)
            supertypeComponents[0]
        else TypeConstants.NOTHING_TYPE

        val subtypeReturnType = if(subtypeComponents[0].syntaxType == SyntaxType.TYPE)
            subtypeComponents[0]
        else TypeConstants.NOTHING_TYPE
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
            if(value.first.name != Types.GENERIC_ID && value.second)
                return false
        }
        return true
    }

    private fun getGenericComponents(statement : JSONStatement) : List<String>
    {
        if(statement.name == Types.GENERIC_ID)
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
            if(elem1.syntaxType == SyntaxType.TYPE || !elem1.name.contains(Tokens.ANON_ARG_ID)) {
                list1.removeFirst()
                continue
            }

            val elem2 = list2[0]
            if(elem2.syntaxType == SyntaxType.TYPE || !elem2.name.contains(Tokens.ANON_ARG_ID)) {
                list2.removeFirst()
                continue
            }

            if(elem1.components[1].name != elem2.components[1].name)
                return false

            list1.removeFirst()
            list2.removeFirst()
        }

        while(list1.isNotEmpty()) {
            if (list1[0].syntaxType == SyntaxType.TYPE || !list1[0].name.contains(Tokens.ANON_ARG_ID))
                list1.removeFirst()
            else break
        }

        while(list2.isNotEmpty()) {
            if (list2[0].syntaxType == SyntaxType.TYPE || !list2[0].name.contains(Tokens.ANON_ARG_ID))
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
            if (component1.syntaxType == SyntaxType.TYPE
                // allow subtype component with default value
                || (!isSuperType && component1.components.size == 3))
                continue
            var matchFound = false
            for (component2 in list2) {
                if (component2.syntaxType == SyntaxType.TYPE)
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
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeList(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = Types.LIST_ID,
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeMethod(type : JSONStatement) : JSONStatement
    {
        val newType = type.copyRemovingLocation()
        newType.components.removeLast()
        return JSONStatement(name = Tokens.METHOD_ID,
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(newType))
    }
}
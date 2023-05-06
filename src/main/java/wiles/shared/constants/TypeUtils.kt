package wiles.shared.constants

import wiles.checker.data.GenericTypesMap
import wiles.checker.statics.InferrerUtils
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.DECLARE_ID
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.Types.COLLECTION_ID
import wiles.shared.constants.Types.DICT_ID
import wiles.shared.constants.Types.LIST_ID

object TypeUtils {

    fun unbox(statement: JSONStatement) : JSONStatement
    {
        assert(statement.syntaxType == SyntaxType.TYPE)
        if(statement.name == Tokens.METHOD_ID || statement.name == Types.METHOD_CALL_ID)
            return statement
        if(statement.name == Tokens.MUTABLE_ID)
            return unbox(statement.components[0])
        if(statement.name == Types.GENERIC_ID)
            return unbox(InferrerUtils.unGenerify(statement.components[1]))
        return statement
    }

    fun removeEmptyEither(statement : JSONStatement) : JSONStatement
    {
        var i = 0
        while(i < statement.components.size) {
            val component = statement.components[i]
            if(component.name == Types.EITHER_ID && component.components.isEmpty())
            {
                statement.components.removeAt(i)
            }
            else {
                statement.components[i] = removeEmptyEither(component)
                i++
            }
        }
        if(statement.name == Types.EITHER_ID && statement.components.size == 1)
            return statement.components[0]
        return statement
    }

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

        else if(subtype.name == Types.UNIVERSAL_SUBTYPE_ID)
            return true

        else if(supertype.name == Types.GENERIC_ID && subtype.name == Types.GENERIC_ID
            && supertype.components[0].name == subtype.components[0].name
            && isFormerSuperTypeOfLatter(supertype.components[1], subtype.components[1], genericTypes = genericTypes,
                getMinus = getMinus))
            return true

        else if(supertype.name == Types.GENERIC_ID && isFormerSuperTypeOfLatter(supertype.components[1], subtype,
                getMinus = getMinus && unboxGenerics, genericTypes = genericTypes)){
            val genName = supertype.components[0].name
            val isDeclaration = supertype.components.getOrNull(2)?.name == DECLARE_ID
            if(genericTypes?.containsKey(genName) == true)
            {
                return if(isFormerSuperTypeOfLatter(genericTypes[genName]!!.first, subtype, unboxGenerics = false,
                        genericTypes = genericTypes) && !isDeclaration) {
                    genericTypes[genName] = Triple(genericTypes[genName]!!.first,true, false)
                    true
                }
                else if(genericTypes[genName]?.third != true && isFormerSuperTypeOfLatter(subtype,
                        genericTypes[genName]!!.first, unboxGenerics = false, genericTypes = genericTypes)) {
                    genericTypes[genName] = Triple(subtype,true, isDeclaration)
                    true
                } else false
            }
            if (genericTypes!=null)
                genericTypes[genName] = Triple(subtype,false, isDeclaration)
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
                        if(isFormerSuperTypeOfLatter(supertypeComponent, subtypeComponent, getMinus = getMinus,
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

        else if (supertype.name == LIST_ID && subtype.name == LIST_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0],subtype.components[0], getMinus = getMinus, genericTypes = genericTypes)

        else if (supertype.name == Tokens.MUTABLE_ID && subtype.name == Tokens.MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], subtype.components[0], getMinus = getMinus,
                genericTypes = genericTypes)

        else if (supertype.name == Types.TYPE_TYPE_ID && subtype.name == Types.TYPE_TYPE_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], subtype.components[0], getMinus = getMinus,
                genericTypes = genericTypes, unboxGenerics = unboxGenerics)


        else if (subtype.name == Tokens.MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype, subtype.components[0], getMinus = getMinus,
                genericTypes = genericTypes)

        else if(supertype.name == Tokens.METHOD_ID && subtype.name == Tokens.METHOD_ID)
            return checkMethodIsSubtype(supertype, subtype, genericTypes?: GenericTypesMap())

        else if(supertype.name == COLLECTION_ID && subtype.name == COLLECTION_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], subtype.components[0], getMinus = getMinus,
                genericTypes = genericTypes) and
                    isFormerSuperTypeOfLatter(supertype.components[1],subtype.components[1], getMinus = getMinus,
                        genericTypes = genericTypes)

        else if(supertype.name == COLLECTION_ID && subtype.name == LIST_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], INT64_TYPE, getMinus = getMinus,
                genericTypes = genericTypes) and
                    isFormerSuperTypeOfLatter(supertype.components[1],subtype.components[0], getMinus = getMinus,
                        genericTypes = genericTypes)

        else if(supertype.name == COLLECTION_ID && subtype.name == DICT_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], subtype.components[0], getMinus = getMinus,
                genericTypes = genericTypes) and
                    isFormerSuperTypeOfLatter(supertype.components[1],subtype.components[1], getMinus = getMinus,
                        genericTypes = genericTypes)

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
            list.addAll(getGenericComponents(component))
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
        return JSONStatement(name = LIST_ID,
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeCollection(key : JSONStatement, value : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = COLLECTION_ID,
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(key.copyRemovingLocation(),value.copyRemovingLocation()))
    }

    fun makeDict(key : JSONStatement, value : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = DICT_ID,
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(key.copyRemovingLocation(),value.copyRemovingLocation()))
    }

    fun makeMethod(type : JSONStatement) : JSONStatement
    {
        val newType = type.copyRemovingLocation()
        newType.components.removeLast()
        return JSONStatement(name = Tokens.METHOD_ID,
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(newType))
    }

    fun makeEither(types: MutableList<JSONStatement>) : JSONStatement
    {
        return JSONStatement(name = Types.EITHER_ID,
            syntaxType = SyntaxType.TYPE,
            components = types.map { it.copyRemovingLocation() }.toMutableList())
    }
}
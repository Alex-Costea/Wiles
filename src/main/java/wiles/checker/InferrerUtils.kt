package wiles.checker

import wiles.checker.CheckerConstants.NOTHING_TYPE
import wiles.checker.exceptions.UnknownIdentifierException
import wiles.checker.exceptions.UnknownTypeException
import wiles.checker.exceptions.UsedBeforeInitializationException
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Chars
import wiles.shared.constants.Predicates
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.DOUBLE_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.LIST_ID
import wiles.shared.constants.Types.STRING_ID

object InferrerUtils {
    fun isFormerSuperTypeOfLatter(supertype : JSONStatement, subtype : JSONStatement) : Boolean
    {
        assert(supertype.type == SyntaxType.TYPE)
        assert(subtype.type == SyntaxType.TYPE)
        if(supertype.toString() == subtype.toString())
            return true

        if(supertype.name == ANYTHING_ID)
        {
            if(subtype.name != EITHER_ID)
            {
                if (isFormerSuperTypeOfLatter(NOTHING_TYPE, subtype))
                    return false
                return true
            }
        }

        else if(supertype.name == EITHER_ID)
        {
            if(subtype.name != EITHER_ID)
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

        else if(subtype.name == EITHER_ID)
        {
            for(component in subtype.components)
            {
                if(!isFormerSuperTypeOfLatter(supertype, component)) {
                    return false
                }
            }
            return true
        }

        else if (supertype.name == LIST_ID && subtype.name == LIST_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0],subtype.components[0])

        else if (subtype.name == MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype, subtype.components[0])

        else if(supertype.name == METHOD_ID && subtype.name == METHOD_ID)
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
        }

        return false
    }

    private fun checkUnnamedArgsInSameOrder(list1: MutableList<JSONStatement>,
                                            list2: MutableList<JSONStatement>) : Boolean
    {
        while(list1.isNotEmpty() && list2.isNotEmpty())
        {
            val elem1 = list1[0]
            if(elem1.type == SyntaxType.TYPE || elem1.name!= ANON_ARG_ID) {
                list1.removeFirst()
                continue
            }

            val elem2 = list2[0]
            if(elem2.type == SyntaxType.TYPE || elem2.name!= ANON_ARG_ID) {
                list2.removeFirst()
                continue
            }

            if(elem1.components[1].name !=elem2.components[1].name)
                return false

            list1.removeFirst()
            list2.removeFirst()
        }

        while(list1.isNotEmpty()) {
            if (list1[0].type == SyntaxType.TYPE || list1[0].name!= ANON_ARG_ID)
                list1.removeFirst()
            else break
        }

        while(list2.isNotEmpty()) {
            if (list2[0].type == SyntaxType.TYPE || list2[0].name!= ANON_ARG_ID)
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

    fun inferTypeFromLiteral(token : JSONStatement, variables : HashMap<String,VariableDetails>) : JSONStatement
    {
        assert(token.type == SyntaxType.TOKEN)
        val name = token.name
        if (Predicates.IS_TEXT_LITERAL.test(name))
            return JSONStatement(STRING_ID, type = SyntaxType.TYPE)
        if (Predicates.IS_NUMBER_LITERAL.test(name))
        {
            if(name.contains(Chars.DECIMAL_DELIMITER))
                return JSONStatement(DOUBLE_ID, type = SyntaxType.TYPE)
            return JSONStatement(INT64_ID, type = SyntaxType.TYPE)
        }
        if(IS_IDENTIFIER.test(name)) {
            if( variables[name]?.initialized==false)
                throw UsedBeforeInitializationException(token.location!!)
            return JSONStatement(
                name = variables[name]?.type?.name ?: throw UnknownIdentifierException(token.location!!),
                type = SyntaxType.TYPE,
                components = variables[name]!!.type.components.map { it.copyRemovingLocation() }.toMutableList())
        }
        throw InternalErrorException("Not one token!")
    }

    fun checkTypeIsDefined(type : JSONStatement)
    {
        if(type.type == SyntaxType.TYPE && type.name.startsWith("!"))
            throw UnknownTypeException(type.getFirstLocation())
        if(type.components.isNotEmpty())
        {
            for(component in type.components)
            {
                checkTypeIsDefined(component)
            }
        }
    }

    fun makeNullable(type: JSONStatement) : JSONStatement
    {
        return JSONStatement(name = EITHER_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation(), NOTHING_TYPE))
    }

    fun makeMutable(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = MUTABLE_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun makeList(type : JSONStatement) : JSONStatement
    {
        return JSONStatement(name = LIST_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation()))
    }

    fun checkMethodAccessCorrect(methodType : JSONStatement, methodCallType : JSONStatement)
        : Map<String,Pair<JSONStatement,Boolean>>?
    {
        // statement, does it need name addition
        val finalCallArgumentsMap = hashMapOf<String,Pair<JSONStatement,Boolean>>()

        val methodComponents = methodType.components[0].components.filter{it.type!=SyntaxType.TYPE}
        val methodComponentsMap = methodComponents.associateBy({it.components[1].name}, { it })
        val callComponents = methodCallType.components[0].components

        //Handle named args
        val namedArgsInMethod = hashMapOf<String,Pair<JSONStatement,Boolean>>()
        val unnamedArgsInMethod = hashMapOf<String,Pair<JSONStatement,Boolean>>()
        for(component in methodComponents)
        {
            if(component.name != ANON_ARG_ID)
                namedArgsInMethod[component.components[1].name]=Pair(component.components[0],
                    component.components.size!=2)
            else unnamedArgsInMethod[component.components[1].name]=Pair(component.components[0],
                component.components.size!=2)
        }

        val namedArgsInCall = hashMapOf<String,JSONStatement>()
        val unnamedArgsInCall = mutableListOf<JSONStatement>()
        for(component in callComponents)
        {
            if(component.components.size>1 && component.components[1].name == ASSIGN_ID) {
                namedArgsInCall[component.components[0].name] = component.components[2].components[0]
                finalCallArgumentsMap[component.components[0].name] = Pair(component,false)
            }
            else unnamedArgsInCall.add(component)
        }

        for(component in namedArgsInCall)
        {
            val name = component.component1()
            val superType = namedArgsInMethod[name]?.first ?:
                unnamedArgsInMethod[name]?.first ?:
                return null
            val subType = component.component2()
            if(isFormerSuperTypeOfLatter(supertype = superType, subtype = subType)) {
                if(name in namedArgsInCall) {
                    namedArgsInMethod.remove(name)
                }
                else if(name in unnamedArgsInMethod) {
                    unnamedArgsInMethod.remove(name)
                }
            }
            else return null
        }

        if(namedArgsInMethod.any { !it.component2().second })
            return null

        val unnamedArgsInMethodList = unnamedArgsInMethod.toList().toMutableList()

        for(component in unnamedArgsInCall.withIndex())
        {
            if(unnamedArgsInMethod.isEmpty())
                return null
            val superType = unnamedArgsInMethodList[component.index].second.first
            val subType = component.value.components[0]
            val name = unnamedArgsInMethodList.removeFirst().first

            finalCallArgumentsMap[name] = Pair(component.value,true)

            if(!isFormerSuperTypeOfLatter(superType,subType))
                return null
        }

        if(unnamedArgsInMethodList.any { !it.component2().second })
            return null

        for(component in namedArgsInMethod)
        {
            val name = component.component1()
            finalCallArgumentsMap[name] = Pair(methodComponentsMap[name]!!.components[2],true)
        }

        return finalCallArgumentsMap
    }
}
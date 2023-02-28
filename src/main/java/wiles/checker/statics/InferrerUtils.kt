package wiles.checker.statics

import wiles.checker.data.VariableDetails
import wiles.checker.data.VariableMap
import wiles.checker.exceptions.*
import wiles.checker.statics.CheckerConstants.NOTHING_TYPE
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.TokenLocation
import wiles.shared.constants.Chars
import wiles.shared.constants.Chars.DIGIT_SEPARATOR
import wiles.shared.constants.Predicates
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens
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
    fun areTypesEquivalent(type1: JSONStatement,type2 : JSONStatement) : Boolean
    {
        return isFormerSuperTypeOfLatter(type1, type2) && isFormerSuperTypeOfLatter(type2, type1)
    }

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

        else if (supertype.name == MUTABLE_ID && subtype.name == MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype.components[0], subtype.components[0])

        else if (subtype.name == MUTABLE_ID)
            return isFormerSuperTypeOfLatter(supertype, subtype.components[0])

        else if(supertype.name == METHOD_ID && subtype.name == METHOD_ID)
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
            if(elem1.type == SyntaxType.TYPE || !elem1.name.contains(ANON_ARG_ID)) {
                list1.removeFirst()
                continue
            }

            val elem2 = list2[0]
            if(elem2.type == SyntaxType.TYPE || !elem2.name.contains(ANON_ARG_ID)) {
                list2.removeFirst()
                continue
            }

            if(elem1.components[1].name !=elem2.components[1].name)
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

    fun inferTypeFromLiteral(token : JSONStatement, variables : HashMap<String, VariableDetails>) : JSONStatement
    {
        assert(token.type == SyntaxType.TOKEN)
        val name = token.name
        if (Predicates.IS_TEXT_LITERAL.test(name))
            return JSONStatement(STRING_ID, type = SyntaxType.TYPE)
        if (Predicates.IS_NUMBER_LITERAL.test(name))
        {
            if(name.contains(Chars.DECIMAL_DELIMITER))
                return JSONStatement(DOUBLE_ID, type = SyntaxType.TYPE)
            try {
                token.name = name.replace(DIGIT_SEPARATOR.toString(),"")
                token.name.substring(1).toLong()
            } catch (e: NumberFormatException) {
                throw InvalidLiteralException(token.getFirstLocation())
            }
            return JSONStatement(INT64_ID, type = SyntaxType.TYPE)
        }
        if(IS_IDENTIFIER.test(name)) {
            if( variables[name]?.initialized==false)
                throw UsedBeforeInitializationException(token.location!!)
            return JSONStatement(
                name = variables[name]?.type?.name ?:
                throw UnknownIdentifierException(token.location!!),
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

    private fun makeEither(types: MutableList<JSONStatement>) : JSONStatement
    {
        return JSONStatement(name = EITHER_ID,
            type = SyntaxType.TYPE,
            components = types.map { it.copyRemovingLocation() }.toMutableList())
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

    fun makeMethod(type : JSONStatement) : JSONStatement
    {
        val newType = type.copyRemovingLocation()
        newType.components.removeLast()
        return JSONStatement(name = METHOD_ID,
            type = SyntaxType.TYPE,
            components = mutableListOf(newType))
    }

    fun getFunctionArguments(methodType : JSONStatement, methodCallType : JSONStatement, location: TokenLocation)
        : Map<String,Pair<JSONStatement,Boolean>>
    {
        // statement, does it need name addition
        val finalCallArgumentsMap = hashMapOf<String,Pair<JSONStatement,Boolean>>()

        val methodComponents = methodType.components[0].components.filter{it.type!=SyntaxType.TYPE}
        val callComponents = methodCallType.components[0].components

        //Create method arguments
        val namedArgsInMethod = hashMapOf<String,Pair<JSONStatement,Boolean>>()
        val unnamedArgsInMethod = hashMapOf<String,Pair<JSONStatement,Boolean>>()
        for(component in methodComponents)
        {
            if(!component.name.contains( ANON_ARG_ID))
                namedArgsInMethod[component.components[1].name]=Pair(component.components[0],
                    component.components.size!=2)
            else unnamedArgsInMethod[component.components[1].name]=Pair(component.components[0],
                component.components.size!=2)
        }

        //create method call arguments
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
                throw CannotCallMethodException(location)
            val subType = component.component2()
            if(isFormerSuperTypeOfLatter(supertype = superType, subtype = subType)) {
                namedArgsInMethod.remove(name)
                unnamedArgsInMethod.remove(name)
            }
            else throw CannotCallMethodException(location)
        }

        if(namedArgsInMethod.any { !it.component2().second })
            throw CannotCallMethodException(location)

        val unnamedArgsInMethodList = unnamedArgsInMethod.toList().toMutableList()

        for(component in unnamedArgsInCall.withIndex())
        {
            if(unnamedArgsInMethodList.isEmpty())
                throw CannotCallMethodException(location)
            val superType = unnamedArgsInMethodList[0].second.first
            val subType = component.value.components[0]
            val name = unnamedArgsInMethodList.removeFirst().first

            finalCallArgumentsMap[name] = Pair(component.value,true)

            if(!isFormerSuperTypeOfLatter(superType,subType))
                throw CannotCallMethodException(location)
        }

        if(unnamedArgsInMethodList.any { !it.component2().second })
            throw CannotCallMethodException(location)

        return finalCallArgumentsMap
    }

    fun unbox(statement: JSONStatement) : JSONStatement
    {
        if(statement.name == MUTABLE_ID)
            return unbox(statement.components[0])
        return statement
    }

    fun getElementTypeFromListType(statement: JSONStatement) : JSONStatement
    {
        val newStatement = unbox(statement)
        if(newStatement.name == LIST_ID)
            return newStatement.components[0]

        if(newStatement.name == EITHER_ID)
        {
            val typesList = mutableListOf<JSONStatement>()
            for(component in statement.components)
            {
                typesList.add(getElementTypeFromListType(component))
            }
            return makeEither(typesList)
        }

        throw InternalErrorException("Couldn't get list's element type")
    }

    private fun containsStopStatement(statement: JSONStatement) : Boolean {
        for (component in statement.components) {
            if (component.type !in arrayListOf(SyntaxType.IF, SyntaxType.FOR, SyntaxType.WHEN)) {
                if (containsStopStatement(component))
                    return true
            }
            if (component.type in arrayListOf(SyntaxType.BREAK, SyntaxType.CONTINUE, SyntaxType.RETURN))
                return true
        }
        return false
    }

    fun addIfNecessary(typeList: MutableList<JSONStatement>, type: JSONStatement) {
        var alreadyExists = false
        for(alreadyExistingType in typeList)
            if(isFormerSuperTypeOfLatter(alreadyExistingType,type))
                alreadyExists = true
        if(!alreadyExists)
            typeList.add(type)
    }

    fun createComponents(statement : JSONStatement, middleName : String = ""): List<JSONStatement> {
        var newComponents = listOf(statement)
        if(middleName != ASSIGN_ID) {
            if(statement.name == EITHER_ID) {
                newComponents = mutableListOf()
                for (component in statement.components) {
                    if(component.name !in EITHER_ID)
                        addIfNecessary(newComponents, component)
                    else for(subComponents in createComponents(component,middleName))
                        addIfNecessary(newComponents, subComponents)
                }
            }
        }
        return newComponents
    }

    fun checkIsInitialized(
        variables: HashMap<String, VariableDetails>,
        listOfVariableMaps: MutableList<VariableMap>,
        codeBlockLists: MutableList<JSONStatement>,
        originalComponents : List<JSONStatement>
    )
    {
        if(originalComponents.none{it.name == Tokens.ELSE_ID }) return
        for(variable in variables.entries)
        {
            if(!variable.value.initialized)
            {
                var isInitialized = true
                var atLeastOne = false
                for(map in listOfVariableMaps)
                {
                    if(containsStopStatement(codeBlockLists.removeFirst())) continue
                    atLeastOne = true
                    if (!map[variable.key]!!.initialized) {
                        isInitialized = false
                        break
                    }
                }
                variables[variable.key]= VariableDetails(variable.value.type,
                    isInitialized && atLeastOne,
                    variable.value.modifiable)
            }
        }
    }
}
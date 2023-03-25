package wiles.checker.statics

import wiles.checker.Checker.Companion.currentFunctionNumber
import wiles.checker.data.CheckerVariableMap
import wiles.checker.data.GenericTypesMap
import wiles.checker.data.VariableDetails
import wiles.checker.exceptions.*
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.TokenLocation
import wiles.shared.constants.Chars
import wiles.shared.constants.Chars.DIGIT_SEPARATOR
import wiles.shared.constants.ErrorMessages.CANNOT_GET_LIST_ELEMENT_TYPE_ERROR
import wiles.shared.constants.ErrorMessages.NOT_ONE_TOKEN_ERROR
import wiles.shared.constants.Predicates
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.TypeUtils.makeMutable
import wiles.shared.constants.Types.DOUBLE_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.GENERIC_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.LIST_ID
import wiles.shared.constants.Types.STRING_ID

object InferrerUtils {
    fun inferTypeFromLiteral(token : JSONStatement, variables : HashMap<String, VariableDetails>) : JSONStatement
    {
        assert(token.syntaxType == SyntaxType.TOKEN)
        val name = token.name
        if (Predicates.IS_TEXT_LITERAL.test(name))
            return JSONStatement(STRING_ID, syntaxType = SyntaxType.TYPE)
        if (Predicates.IS_NUMBER_LITERAL.test(name))
        {
            if(name.contains(Chars.DECIMAL_DELIMITER))
                return JSONStatement(DOUBLE_ID, syntaxType = SyntaxType.TYPE)
            try {
                token.name = name.replace(DIGIT_SEPARATOR.toString(),"")
                token.name.substring(1).toLong()
            } catch (e: NumberFormatException) {
                throw InvalidLiteralException(token.getFirstLocation())
            }
            return JSONStatement(INT64_ID, syntaxType = SyntaxType.TYPE)
        }
        if(IS_IDENTIFIER.test(name)) {
            if( variables[name]?.initialized==false)
                throw UsedBeforeInitializationException(token.getFirstLocation())
            return JSONStatement(
                name = variables[name]?.type?.name ?:
                throw UnknownIdentifierException(token.getFirstLocation()),
                syntaxType = SyntaxType.TYPE,
                components = variables[name]!!.type.components.map { it.copyRemovingLocation() }.toMutableList())
        }
        throw InternalErrorException(NOT_ONE_TOKEN_ERROR)
    }

    fun createGenericType(
        type: JSONStatement,
        typeNames: MutableMap<String, JSONStatement>,
        variables: CheckerVariableMap
    )
    {
        val name = getTypeNumber(type.name)
        if(type.name == GENERIC_ID)
        {
            type.components[0].name=getTypeNumber(type.components[0].name)
            return
        }
        else if(type.syntaxType == SyntaxType.CODE_BLOCK)
        {
            return
        }
        else if(variables.containsKey(type.name))
            throw VariableAlreadyDeclaredException(type.getFirstLocation())
        else if(typeNames.containsKey(name))
        {
            val newType = typeNames[name]!!
            assert(type.components.isEmpty())
            type.components.add(JSONStatement(name = name, syntaxType = SyntaxType.TOKEN))
            type.components.add(newType)
            type.name = GENERIC_ID
            return
        }
        else if(type.syntaxType == SyntaxType.TYPE && IS_IDENTIFIER.test(type.name) && type.name != NOTHING_ID)
            throw UnknownTypeException(type.getFirstLocation())
        if(type.components.isNotEmpty())
        {
            for(component in type.components)
            {
                createGenericType(component, typeNames, variables)
            }
        }
    }

    fun getTypeNumber(name : String) : String
    {
        if(name.contains("|"))
            return name
        return "$name|$currentFunctionNumber"
    }

    fun makeNullable(type: JSONStatement) : JSONStatement
    {
        return JSONStatement(name = EITHER_ID,
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(type.copyRemovingLocation(), NOTHING_TYPE))
    }

    fun makeGeneric(type: JSONStatement, name : String) : JSONStatement
    {
        return JSONStatement(name = GENERIC_ID,
            syntaxType = SyntaxType.TYPE,
            components = mutableListOf(
                JSONStatement(syntaxType = SyntaxType.TOKEN, name = name),
                type.copyRemovingLocation()))
    }

    private fun makeEither(types: MutableList<JSONStatement>) : JSONStatement
    {
        return JSONStatement(name = EITHER_ID,
            syntaxType = SyntaxType.TYPE,
            components = types.map { it.copyRemovingLocation() }.toMutableList())
    }

    fun specifyGenericTypesForFunction(statement : JSONStatement, genericTypes : GenericTypesMap)
    {
        if(statement.syntaxType == SyntaxType.TYPE && statement.name == GENERIC_ID)
        {
            val name = getTypeNumber(statement.components[0].name)
            if(genericTypes.containsKey(name))
            {
                statement.components[1] = genericTypes[name]!!.first
            }
            return
        }
        for(component in statement.components)
            specifyGenericTypesForFunction(component, genericTypes)
    }

    fun getFunctionArguments(methodType : JSONStatement, methodCallType : JSONStatement,
                             location: TokenLocation, genericTypes : GenericTypesMap)
        : Map<String,Pair<JSONStatement,Boolean>>
    {
        // statement, does it need name addition
        val finalCallArgumentsMap = hashMapOf<String,Pair<JSONStatement,Boolean>>()

        val methodComponents = methodType.components[0].components.filter{it.syntaxType!=SyntaxType.TYPE}
        val callComponents = methodCallType.components[0].components

        //Create method arguments
        val namedArgsInMethod = hashMapOf<String,Pair<JSONStatement,Boolean>>()
        val unnamedArgsInMethod = linkedMapOf<String,Pair<JSONStatement,Boolean>>()
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
            if(isFormerSuperTypeOfLatter(superType, subType, genericTypes = genericTypes)) {
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

            if(!isFormerSuperTypeOfLatter(superType, subType, genericTypes = genericTypes))
                throw CannotCallMethodException(location)

        }

        if(unnamedArgsInMethodList.any { !it.component2().second })
            throw CannotCallMethodException(location)

        return finalCallArgumentsMap
    }

    fun unbox(statement: JSONStatement) : JSONStatement
    {
        assert(statement.syntaxType == SyntaxType.TYPE)
        if(statement.name == MUTABLE_ID)
            return unbox(statement.components[0])
        if(statement.name == GENERIC_ID)
            return unbox(unGenerify(statement.components[1]))
        return statement
    }

    fun unGenerify(statement : JSONStatement, variableMap: CheckerVariableMap? = null) : JSONStatement
    {
        if(statement.syntaxType == SyntaxType.TYPE && statement.name == GENERIC_ID &&
            (variableMap == null || !variableMap.containsKey(statement.components[0].name)))
        {
            statement.name = statement.components[1].name
            statement.location = statement.components[1].location
            statement.components = statement.components[1].components
        }
        for(component in statement.components)
            unGenerify(component)
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

        throw InternalErrorException(CANNOT_GET_LIST_ELEMENT_TYPE_ERROR)
    }

    private fun containsStopStatement(statement: JSONStatement) : Boolean {
        for (component in statement.components) {
            if (component.syntaxType !in arrayListOf(SyntaxType.IF, SyntaxType.FOR, SyntaxType.WHEN)) {
                if (containsStopStatement(component))
                    return true
            }
            if (component.syntaxType in arrayListOf(SyntaxType.BREAK, SyntaxType.CONTINUE, SyntaxType.RETURN))
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
                val componentsList = statement.copyRemovingLocation().components
                for (component in componentsList) {
                    if(component.name == MUTABLE_ID && component.components[0].name == EITHER_ID)
                    {
                        val newNewComp = component.components[0].components.map { makeMutable(it) }
                        for(comp in newNewComp)
                        {
                            addIfNecessary(newComponents, comp)
                        }
                    }
                    else if(component.name != EITHER_ID)
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
        listOfVariableMaps: MutableList<CheckerVariableMap>,
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
package wiles.checker.statics

import wiles.checker.data.CheckerContext
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
import wiles.shared.constants.ErrorMessages.CONFLICTING_TYPES_FOR_IDENTIFIER_ERROR
import wiles.shared.constants.ErrorMessages.EXPECTED_VALUE_FOR_IDENTIFIER_ERROR
import wiles.shared.constants.ErrorMessages.NOT_ONE_TOKEN_ERROR
import wiles.shared.constants.ErrorMessages.NO_MATCH_FOR_ERROR
import wiles.shared.constants.ErrorMessages.TOO_MANY_VALUES_PROVIDED_ERROR
import wiles.shared.constants.Predicates
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.ANON_ARG_ID
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.TYPEDEF_ID
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeUtils
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.TypeUtils.makeEither
import wiles.shared.constants.TypeUtils.makeMutable
import wiles.shared.constants.TypeUtils.removeEmptyEither
import wiles.shared.constants.TypeUtils.makeTypeUngeneric
import wiles.shared.constants.TypeUtils.makeTypeUnmutable
import wiles.shared.constants.Types.DECIMAL_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.GENERIC_ID
import wiles.shared.constants.Types.INT_ID
import wiles.shared.constants.Types.LIST_ID
import wiles.shared.constants.Types.STRING_ID
import wiles.shared.constants.Types.TYPE_TYPE_ID

object InferrerUtils {
    fun inferTypeFromLiteral(token : JSONStatement,
                             variables : HashMap<String, VariableDetails>) : JSONStatement
    {
        assert(token.syntaxType == SyntaxType.TOKEN)
        val name = token.name
        if (Predicates.IS_TEXT_LITERAL.test(name))
            return JSONStatement(STRING_ID, syntaxType = SyntaxType.TYPE)
        if (Predicates.IS_NUMBER_LITERAL.test(name))
        {
            if(name.contains(Chars.DECIMAL_DELIMITER))
                return JSONStatement(DECIMAL_ID, syntaxType = SyntaxType.TYPE)
            token.name = name.replace(DIGIT_SEPARATOR.toString(),"")
            return JSONStatement(INT_ID, syntaxType = SyntaxType.TYPE)
        }
        if(IS_IDENTIFIER.test(name)) {
            if( variables[name]?.initialized==false)
                throw UsedBeforeInitializationException(token.getFirstLocation())
            val value = variables[name]
            return JSONStatement(
                name = value?.type?.name ?:
                throw UnknownIdentifierException(token.getFirstLocation()),
                syntaxType = SyntaxType.TYPE,
                components = value.type.components.map { it.copyRemovingLocation() }.toMutableList())
        }
        throw InternalErrorException(NOT_ONE_TOKEN_ERROR)
    }

    private fun checkStatementContainsReference(statement1 : JSONStatement, statement2 : JSONStatement)
    {
        if(statement1 === statement2)
            throw RecursiveTypeDefinitionException(statement1.getFirstLocation())
        for(component in statement1.components)
        {
            checkStatementContainsReference(component,statement2)
        }
    }

    fun createTypes(
        type: JSONStatement,
        typeNames: MutableMap<String, JSONStatement>,
        variables: CheckerVariableMap,
        context: CheckerContext
    )
    {
        val name = getTypeNumber(type.name, context)
        if(type.name == GENERIC_ID)
        {
            if(variables.containsKey(type.components[0].name))
                throw VariableAlreadyDeclaredException(type.components[0].getFirstLocation())
            type.components[0].name=getTypeNumber(type.components[0].name, context)
            type.components.add(JSONStatement(name = Tokens.DECLARE_ID, syntaxType = SyntaxType.TOKEN))
            createTypes(type.components[1], typeNames, variables, context)
            return
        }
        else if(type.syntaxType == SyntaxType.CODE_BLOCK)
        {
            return
        }
        else if(typeNames.containsKey(name))
        {
            val newType = typeNames[name]!!
            checkStatementContainsReference(newType, type)
            assert(type.components.isEmpty())
            type.components.add(JSONStatement(name = name, syntaxType = SyntaxType.TOKEN))
            type.components.add(newType)
            type.name = GENERIC_ID
            return
        }
        else if(type.syntaxType == SyntaxType.TYPE && IS_IDENTIFIER.test(type.name)) {
            if(type.name == NOTHING_ID)
                return
            val nameOfTypeDef = type.name + "|" + TYPEDEF_ID
            if(variables.containsKey(nameOfTypeDef))
            {
                val newType = variables[nameOfTypeDef]!!.type.components[0]
                type.name = newType.name
                type.syntaxType = newType.syntaxType
                type.components = newType.components
                return
            }
            else throw UnknownTypeException(type.getFirstLocation())
        }
        if(type.components.isNotEmpty())
        {
            for(component in type.components)
            {
                createTypes(component, typeNames, variables, context)
            }
        }
    }

    fun getTypeNumber(name : String, context: CheckerContext) : String
    {
        if(name.contains("|"))
            return name
        return "$name|${context.currentFunctionNumber}"
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

    fun makeGenericDeclaration(type: JSONStatement) : JSONStatement
    {
        val copy = type.copy()
        copy.components.add(JSONStatement(name = Tokens.DECLARE_ID, syntaxType = SyntaxType.TOKEN))
        return copy
    }

    fun specifyGenericTypesForFunction(statement : JSONStatement, genericTypes: GenericTypesMap, context: CheckerContext)
    {
        if(statement.syntaxType == SyntaxType.TYPE && statement.name == GENERIC_ID)
        {
            val name = getTypeNumber(statement.components[0].name, context)
            if(genericTypes.containsKey(name))
            {
                statement.components[1] = genericTypes[name]!!.statement
            }
            return
        }
        for(component in statement.components)
            specifyGenericTypesForFunction(component, genericTypes, context)
    }

    fun getFunctionArguments(methodType : JSONStatement, methodCallType : JSONStatement,
                             location: TokenLocation, genericTypes : GenericTypesMap)
        : Map<String,Pair<JSONStatement,Boolean>>
    {
        // statement, does it need name addition
        val finalCallArgumentsMap = linkedMapOf<String,Pair<JSONStatement,Boolean>>()

        val methodComponents = methodType.components[0].components.filter{it.syntaxType!=SyntaxType.TYPE}
        val callComponents = methodCallType.components[0].components

        //Create method arguments
        val namedArgsInMethod = linkedMapOf<String,Pair<JSONStatement,Boolean>>()
        val unnamedArgsInMethod = linkedMapOf<String,Pair<JSONStatement,Boolean>>()
        for(component in methodComponents)
        {
            if(!component.name.contains(ANON_ARG_ID))
                namedArgsInMethod[component.components[1].name]=Pair(component.components[0],
                    component.components.size!=2)
            else unnamedArgsInMethod[component.components[1].name]=Pair(component.components[0],
                component.components.size!=2)
        }

        //create method call arguments
        val namedArgsInCall = linkedMapOf<String,JSONStatement>()
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
                throw CannotCallMethodException(location,NO_MATCH_FOR_ERROR.format(name.drop(1)))
            val subType = component.component2()
            if(isFormerSuperTypeOfLatter(superType, subType, genericTypes = genericTypes)) {
                namedArgsInMethod.remove(name)
                unnamedArgsInMethod.remove(name)
            }
            else throw CannotCallMethodException(location,CONFLICTING_TYPES_FOR_IDENTIFIER_ERROR
                .format(clarifyGenericType(superType, genericTypes),subType,name.drop(1)))
        }

        val namedArgsUnmatched = namedArgsInMethod.filter { !it.component2().second }
        if(namedArgsUnmatched.isNotEmpty())
            throw CannotCallMethodException(location,EXPECTED_VALUE_FOR_IDENTIFIER_ERROR.format(
                namedArgsUnmatched.entries.first().key.drop(1)))

        val unnamedArgsInMethodList = unnamedArgsInMethod.toList().toMutableList()

        for(component in unnamedArgsInCall.withIndex())
        {
            if(unnamedArgsInMethodList.isEmpty())
                throw CannotCallMethodException(location,TOO_MANY_VALUES_PROVIDED_ERROR)
            val superType = unnamedArgsInMethodList[0].second.first
            val subType = component.value.components[0]
            val name = unnamedArgsInMethodList.removeFirst().first

            finalCallArgumentsMap[name] = Pair(component.value,true)

            if(!isFormerSuperTypeOfLatter(superType, subType, genericTypes = genericTypes))
                throw CannotCallMethodException(location, CONFLICTING_TYPES_FOR_IDENTIFIER_ERROR
                    .format(clarifyGenericType(superType, genericTypes),subType,name.drop(1)))
        }

        val unnamedArgsUnmatched = unnamedArgsInMethodList.filter { !it.component2().second }
        if(unnamedArgsUnmatched.isNotEmpty())
            throw CannotCallMethodException(location,EXPECTED_VALUE_FOR_IDENTIFIER_ERROR.format(
                unnamedArgsUnmatched.first().first.drop(1)))

        return finalCallArgumentsMap
    }

    private fun clarifyGenericType(statement: JSONStatement, genericTypes: GenericTypesMap): JSONStatement {
        return clarifyGenericTypeComponent(statement.copyRemovingLocation(), genericTypes)
    }

    private fun clarifyGenericTypeComponent(statement: JSONStatement, genericTypes: GenericTypesMap) : JSONStatement
    {
        val name = statement.components.getOrNull(0)?.name
        if(statement.name == GENERIC_ID && name in genericTypes.keys)
        {
            val newType = genericTypes[name]!!.statement.copyRemovingLocation()
            statement.name = newType.name
            statement.syntaxType = newType.syntaxType
            statement.location = null
            statement.components = newType.components
        }
        for(component in statement.components)
            clarifyGenericTypeComponent(component, genericTypes)
        return statement
    }

    fun unGenerify(statement : JSONStatement, variableMap: CheckerVariableMap? = null) : JSONStatement
    {
        val name = statement.components.getOrNull(0)?.name
        val type = if(name==null) null
                   else variableMap?.getOrDefault(name.split("|")[0],null)?.type
        if(statement.syntaxType == SyntaxType.TYPE && statement.name == GENERIC_ID &&
            (variableMap == null || type?.name != TYPE_TYPE_ID || type.components[0].components[0].name != name))
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
        val newStatement = makeTypeUnmutable(statement)
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
            if (component.syntaxType !in arrayListOf(SyntaxType.IF, SyntaxType.FOR, SyntaxType.WHEN, SyntaxType.METHOD)) {
                if (containsStopStatement(component))
                    return true
            }
            if (component.syntaxType in arrayListOf(SyntaxType.BREAK, SyntaxType.CONTINUE, SyntaxType.RETURN))
                return true
            if(component.syntaxType == SyntaxType.EXPRESSION && component.components.size == 4
                && component.components[1].name == "!panic" && component.components[2].name.contains(APPLY_ID))
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

    fun addType(resultingType: JSONStatement, addedType: JSONStatement): JSONStatement {
        return if(isFormerSuperTypeOfLatter(resultingType, addedType))
            resultingType
        else if(isFormerSuperTypeOfLatter(addedType, resultingType))
            addedType
        else if(resultingType.name == EITHER_ID) {
            resultingType.components.add(addedType)
            resultingType
        } else JSONStatement(name = EITHER_ID, syntaxType = SyntaxType.TYPE,
            components = mutableListOf(resultingType,addedType))
    }

    fun checkVarsAfterConditional(
        variables: HashMap<String, VariableDetails>,
        listOfVariableMaps: MutableList<CheckerVariableMap>,
        codeBlockLists: MutableList<JSONStatement>,
        originalComponents : List<JSONStatement>
    )
    {
        val hasElse = originalComponents.any {it.name == Tokens.ELSE_ID }
        val variableNameForWhen = originalComponents.getOrNull(0)?.components?.getOrNull(0)?.name
        for(variable in variables.entries)
        {
            val shouldGetType = variable.key == variableNameForWhen
            val isAlreadyInitialized = variable.value.initialized
            val codeBlocks = codeBlockLists.toMutableList()
            if((!isAlreadyInitialized) || shouldGetType) {
                var isInitializedHere = true
                var atLeastOne = false
                var type : JSONStatement = variable.value.type.copy()
                for (map in listOfVariableMaps) {
                    if (containsStopStatement(codeBlocks.removeFirst())) {
                        if(shouldGetType)
                        {
                            val typeToRemove = map[variable.key]!!.type.copy()
                            TypeUtils.getTypeMinusType(typeToRemove, type)
                            type = removeEmptyEither(type)
                        }
                        continue
                    }
                    atLeastOne = true
                    if (!map[variable.key]!!.initialized) {
                        isInitializedHere = false
                        break
                    }
                }
                variables[variable.key] = VariableDetails(
                    if(type.name == EITHER_ID && type.components.isEmpty()) variable.value.type else type,
                    isAlreadyInitialized || (isInitializedHere && atLeastOne && hasElse),
                    variable.value.modifiable
                )
            }
        }
    }
}
package wiles.checker.inferrers

import wiles.checker.data.GenericTypesMap
import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.CannotModifyException
import wiles.checker.exceptions.ExpectedIdentifierException
import wiles.checker.exceptions.WrongOperationException
import wiles.checker.services.InferrerService
import wiles.checker.statics.InferrerUtils
import wiles.checker.statics.InferrerUtils.addIfNecessary
import wiles.checker.statics.InferrerUtils.createComponents
import wiles.checker.statics.InferrerUtils.inferTypeFromLiteral
import wiles.checker.statics.InferrerUtils.specifyGenericTypesForFunction
import wiles.checker.statics.InferrerUtils.unGenerify
import wiles.checker.statics.SimpleTypeGenerator.getSimpleTypes
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.IRREGULAR_STATEMENT_ERROR
import wiles.shared.constants.ErrorMessages.UNKNOWN_SYNTAX_TYPE_ERROR
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.AND_ID
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.EQUALS_ID
import wiles.shared.constants.Tokens.IMPORT_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.NOT_EQUAL_ID
import wiles.shared.constants.Tokens.OR_ID
import wiles.shared.constants.TypeConstants
import wiles.shared.constants.TypeConstants.NOTHING_TOKEN
import wiles.shared.constants.TypeUtils.makeList
import wiles.shared.constants.TypeUtils.makeMethod
import wiles.shared.constants.TypeUtils.makeTypeUngeneric
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.BOOLEAN_ID
import wiles.shared.constants.Types.DOUBLE_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.INT_ID
import wiles.shared.constants.Types.LIST_ID
import wiles.shared.constants.Types.METHOD_CALL_ID
import wiles.shared.constants.Types.STRING_ID

class InferFromExpression(details: InferrerDetails) : InferFromStatement(details) {

    private lateinit var operationName : String

    private fun getTypeOfExpression(left : JSONStatement, middle : JSONStatement, right: JSONStatement) : JSONStatement
    {
        assert(left.syntaxType == SyntaxType.TYPE)
        assert(middle.syntaxType == SyntaxType.TOKEN)
        assert(right.syntaxType == SyntaxType.TYPE)

        val unboxedLeft = makeTypeUngeneric(left)
        val unboxedRight = makeTypeUngeneric(right)

        val leftComponents = createComponents(unboxedLeft,middle.name)

        val rightComponents = createComponents(unboxedRight,middle.name)

        val resultingTypes : MutableList<JSONStatement> = mutableListOf()
        var isValid = true
        for(newLeft in leftComponents)
        {
            for(newRight in rightComponents)
            {
                var unboxedNewLeft = makeTypeUngeneric(newLeft)
                val type = getSimpleTypes(Triple(newLeft, middle, newRight)) ?:
                    if(unboxedNewLeft.name == METHOD_ID &&
                            middle == TypeConstants.APPLY_OPERATION &&
                            newRight.name == METHOD_CALL_ID) {
                        val genericTypes = GenericTypesMap()
                        val result = InferrerUtils.getFunctionArguments(unboxedNewLeft, newRight,
                            middle.getFirstLocation(), genericTypes)
                        unboxedNewLeft = unboxedNewLeft.copy()
                        specifyGenericTypesForFunction(unboxedNewLeft, genericTypes, context)
                        val newResult = result.map {
                            if(it.value.second)
                                JSONStatement(syntaxType = SyntaxType.EXPRESSION,
                                    components = mutableListOf(
                                        JSONStatement(syntaxType = SyntaxType.TOKEN, name = it.key),
                                        JSONStatement(syntaxType = SyntaxType.TOKEN, name = ASSIGN_ID),
                                        it.value.first
                                    ))
                            else it.value.first
                        }.toMutableList()
                        //set parameters in method call
                        newRight.components[0].components = newResult

                        //return
                        unGenerify(unboxedNewLeft.components[0].components[0].copy(), variables)
                    } else null

                if(type != null) {
                    addIfNecessary(resultingTypes,type)
                }
                else isValid = false
            }
        }
        if(!isValid)
            throw WrongOperationException(middle.getFirstLocation(),unboxedLeft.toString(),unboxedRight.toString())
        if(resultingTypes.isNotEmpty())
        {
            var leftText : String = if(leftComponents.size == 1) makeTypeUngeneric(leftComponents[0]).name else ANYTHING_ID
            if(leftText !in VALID_NAMED) leftText = ANYTHING_ID
            var rightText : String = if(rightComponents.size == 1) makeTypeUngeneric(rightComponents[0]).name else ANYTHING_ID
            if(rightText !in VALID_NAMED) rightText = ANYTHING_ID
            operationName = if(middle.name in listOf(ASSIGN_ID, MUTABLE_ID,
                    IMPORT_ID, AND_ID, OR_ID, EQUALS_ID, NOT_EQUAL_ID)) middle.name
                else "${leftText}|${middle.name}|${rightText}"
            return if(resultingTypes.size == 1)
                resultingTypes[0]
            else JSONStatement(name = EITHER_ID, syntaxType = SyntaxType.TYPE, components = resultingTypes)
        }
        throw WrongOperationException(middle.getFirstLocation(),unboxedLeft.toString(),unboxedRight.toString())
    }

    override fun infer() {
        if(statement.syntaxType in TYPES_LIST)
        {
            val inferrer = InferrerService(InferrerDetails(
                statement,variables, CompilationExceptionsCollection(),additionalVars, context))
            inferrer.infer()
            exceptions.addAll(inferrer.exceptions)
        }
        else if(statement.components.size==1 && statement.components[0].syntaxType in TYPES_LIST)
        {
            val inferrer = InferrerService(
                InferrerDetails(statement.components[0],
                variables, CompilationExceptionsCollection(), additionalVars, context)
            )
            inferrer.infer()
            when (statement.components[0].syntaxType) {
                SyntaxType.LIST -> {
                    //set list type
                    val newListType = JSONStatement(syntaxType = SyntaxType.TYPE,
                        name = LIST_ID,
                        components = mutableListOf(statement.components[0].components[0]))
                    statement.components.add(0, newListType)
                }
                SyntaxType.DICT -> statement.components.add(0, statement.components[0].components[0])
                SyntaxType.METHOD -> {
                    val newType = statement.components[0].copyRemovingLocation()
                    newType.components.removeLast()
                    statement.components.add(0, JSONStatement(syntaxType = SyntaxType.TYPE,
                        name = METHOD_ID,
                        components = mutableListOf(newType)))
                }
                else -> throw InternalErrorException(UNKNOWN_SYNTAX_TYPE_ERROR)
            }
            exceptions.addAll(inferrer.exceptions)
        }
        else if(statement.components.size==1 && statement.components[0].syntaxType == SyntaxType.TOKEN)
        {
            val type = inferTypeFromLiteral(statement.components[0],variables, additionalVars)
            statement.components.add(0,type)
        }
        else if (statement.components.size == 2 || statement.components.size == 3)
        {
            if(statement.components.first().syntaxType == SyntaxType.TYPE)
                return
            assert(statement.syntaxType == SyntaxType.EXPRESSION)

            val isThree = statement.components.size == 3

            val left = if(isThree) statement.components[0] else NOTHING_TOKEN
            val middle = if(isThree) statement.components[1] else statement.components[0]
            val right = if(isThree) statement.components[2] else statement.components[1]

            val validWithZeroComponentsTypesList = listOf(SyntaxType.TYPE, SyntaxType.TOKEN, SyntaxType.METHOD_CALL)
            assert(left.components.size > 0 || left.syntaxType in validWithZeroComponentsTypesList)
            assert(right.components.size > 0 || right.syntaxType in validWithZeroComponentsTypesList)

            //Check if value can be assigned to
            if(middle.name == ASSIGN_ID)
            {
                //Import statement
                if(left.components.size == 2 && left.components.all { it.syntaxType == SyntaxType.TOKEN }
                    && left.components[0].name == IMPORT_ID) {
                    left.name = ""
                    left.syntaxType = SyntaxType.EXPRESSION
                    val name = left.components[1].name
                    val location = left.components[1].getFirstLocation()
                    left.components.clear()
                    left.components.add(
                        JSONStatement(
                            name = "!$IMPORT_ID$name",
                            syntaxType = SyntaxType.TOKEN,
                            location = location
                    ))
                }

                //Change value of variable
                if(left.components.size == 1 && IS_IDENTIFIER.test(left.components[0].name)) {
                    val variableName = left.components[0].name
                    if(variables.containsKey(variableName)) {
                        if (variables[variableName]?.modifiable != true && variables[variableName]?.initialized != false)
                            throw CannotModifyException(left.getFirstLocation())
                        variables[variableName]?.initialized = true
                    }
                }
                else throw ExpectedIdentifierException(left.getFirstLocation())
            }

            val leftIsToken = left.syntaxType == SyntaxType.TOKEN
            val rightIsToken = right.syntaxType == SyntaxType.TOKEN

            if(!leftIsToken)
                InferFromExpression(InferrerDetails(left, variables, exceptions, additionalVars, context)).infer()

            val leftType = if(leftIsToken) inferTypeFromLiteral(left,variables, additionalVars)
                else if(left.syntaxType == SyntaxType.EXPRESSION) left.components[0]
                else if(left.syntaxType == SyntaxType.DICT) left.components[0]
                else if(left.syntaxType == SyntaxType.LIST) makeList(left.components[0])
                else if(left.syntaxType == SyntaxType.METHOD) makeMethod(left)
                else throw InternalErrorException()

            //Transform access operation into apply operation
            if(middle == TypeConstants.ACCESS_OPERATION)
            {
                var methodCallComponents = mutableListOf<JSONStatement>()
                if(right.syntaxType!=SyntaxType.TOKEN) {
                    if(right.syntaxType == SyntaxType.EXPRESSION &&
                        right.components.getOrNull(1)?.name == APPLY_ID) {
                        methodCallComponents = right.components[2].components
                        right.name = right.components[0].name
                        right.syntaxType = right.components[0].syntaxType
                        right.location = right.components[0].location
                        right.components = right.components[0].components
                    }
                }
                else right.name = right.name

                //create correct components
                assert(isThree)
                middle.name = APPLY_ID
                val oldLeft = if(statement.components[0].syntaxType==SyntaxType.EXPRESSION)
                    statement.components[0]
                else JSONStatement(syntaxType = SyntaxType.EXPRESSION,
                    components = mutableListOf(statement.components[0]))
                statement.components[0] = statement.components[2]
                methodCallComponents.add(0,oldLeft)
                statement.components[2] = JSONStatement(syntaxType = SyntaxType.METHOD_CALL,
                    components = methodCallComponents)

                //redo infer
                infer()
                return
            }

            if(!rightIsToken)
                InferFromExpression(InferrerDetails(right, variables, exceptions, additionalVars, context)).infer()

            val middleIsImport = middle.name == IMPORT_ID

            val rightType = if(rightIsToken && !middleIsImport) inferTypeFromLiteral(right,variables, additionalVars)
                else if(rightIsToken && IS_IDENTIFIER.test(right.name))
                    inferTypeFromLiteral(right, additionalVars, additionalVars)
                else if(middleIsImport) throw ExpectedIdentifierException(right.getFirstLocation())
                else if(right.syntaxType == SyntaxType.EXPRESSION) right.components[0]
                else if(right.syntaxType == SyntaxType.DICT) right.components[0]
                else if(right.syntaxType == SyntaxType.LIST) makeList(right.components[0])
                else if(right.syntaxType == SyntaxType.METHOD) makeMethod(right)
                else if(right.syntaxType == SyntaxType.METHOD_CALL) right.components[0]
                else throw InternalErrorException()

            statement.components.add(0,getTypeOfExpression(leftType,middle,rightType))

            // simplify format
            if(right.syntaxType == SyntaxType.METHOD_CALL)
            {
                right.components = right.components[0].components[0].components
            }

            middle.name = operationName
        }
        else if(statement.components.size==4)
            return
        else throw InternalErrorException(IRREGULAR_STATEMENT_ERROR)
    }

    companion object {
        val VALID_NAMED = arrayListOf(
            METHOD_ID,DOUBLE_ID, INT_ID, STRING_ID, BOOLEAN_ID, LIST_ID, ANYTHING_ID, NOTHING_ID, METHOD_CALL_ID)

        val TYPES_LIST =
            listOf(SyntaxType.METHOD, SyntaxType.LIST, SyntaxType.METHOD_CALL, SyntaxType.TYPE, SyntaxType.DICT)
    }
}
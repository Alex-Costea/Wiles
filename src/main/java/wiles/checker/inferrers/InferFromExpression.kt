package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.CannotModifyException
import wiles.checker.exceptions.ExpectedIdentifierException
import wiles.checker.exceptions.WrongOperationException
import wiles.checker.services.AccessOperationIdentifiers
import wiles.checker.services.InferrerService
import wiles.checker.statics.InferrerUtils
import wiles.checker.statics.InferrerUtils.addIfNecessary
import wiles.checker.statics.InferrerUtils.createComponents
import wiles.checker.statics.InferrerUtils.inferTypeFromLiteral
import wiles.checker.statics.InferrerUtils.unbox
import wiles.checker.statics.SimpleTypeGenerator.getSimpleTypes
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.AND_ID
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.IMPORT_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MODIFY_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Tokens.NEW_ID
import wiles.shared.constants.Tokens.NOTHING_ID
import wiles.shared.constants.Tokens.OR_ID
import wiles.shared.constants.TypeConstants
import wiles.shared.constants.TypeConstants.NOTHING_TOKEN
import wiles.shared.constants.TypeConstants.makeList
import wiles.shared.constants.TypeConstants.makeMethod
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.BOOLEAN_ID
import wiles.shared.constants.Types.DOUBLE_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.INT64_ID
import wiles.shared.constants.Types.LIST_ID
import wiles.shared.constants.Types.METHOD_CALL_ID
import wiles.shared.constants.Types.STRING_ID

class InferFromExpression(details: InferrerDetails) : InferFromStatement(details) {

    private lateinit var operationName : String



    private fun getTypeOfExpression(left : JSONStatement, middle : JSONStatement, right: JSONStatement) : JSONStatement
    {
        assert(left.type == SyntaxType.TYPE)
        assert(middle.type == SyntaxType.TOKEN)
        assert(right.type == SyntaxType.TYPE)

        val leftComponents = createComponents(left,middle.name)

        val rightComponents = createComponents(right,middle.name)


        val resultingTypes : MutableList<JSONStatement> = mutableListOf()
        var isValid = true
        for(newLeft in leftComponents)
        {
            for(newRight in rightComponents)
            {
                val unboxedNewLeft = unbox(newLeft)
                val type = getSimpleTypes(Triple(newLeft, middle, newRight)) ?:
                    if(unboxedNewLeft.name == METHOD_ID &&
                            middle == TypeConstants.APPLY_OPERATION &&
                            newRight.name == METHOD_CALL_ID) {
                        val result = InferrerUtils.getFunctionArguments(unboxedNewLeft, newRight,
                            middle.getFirstLocation())
                        val newResult = result.map {
                            if(it.value.second)
                                JSONStatement(type = SyntaxType.EXPRESSION,
                                    components = mutableListOf(
                                        JSONStatement(type = SyntaxType.TOKEN, name = it.key),
                                        JSONStatement(type = SyntaxType.TOKEN, name = ASSIGN_ID),
                                        it.value.first
                                    ))
                            else it.value.first
                        }.toMutableList()

                        //set parameters in method call
                        newRight.components[0].components = newResult

                        //return
                        unboxedNewLeft.components[0].components[0]

                    } else null

                if(type != null) {
                    addIfNecessary(resultingTypes,type)
                }
                else isValid = false
            }
        }
        if(!isValid)
            throw WrongOperationException(middle.getFirstLocation(),left.toString(),right.toString())
        if(resultingTypes.isNotEmpty())
        {
            var leftText : String = if(leftComponents.size == 1) unbox(leftComponents[0]).name else ANYTHING_ID
            if(leftText !in VALID_NAMED) leftText = ANYTHING_ID
            var rightText : String = if(rightComponents.size == 1) unbox(rightComponents[0]).name else ANYTHING_ID
            if(rightText !in VALID_NAMED) rightText = ANYTHING_ID
            operationName = if(middle.name in listOf(ASSIGN_ID, MUTABLE_ID, NEW_ID,
                    IMPORT_ID, MODIFY_ID, AND_ID, OR_ID)) middle.name
                else "${leftText}|${middle.name}|${rightText}"
            return if(resultingTypes.size == 1)
                resultingTypes[0]
            else JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE, components = resultingTypes)
        }
        throw WrongOperationException(middle.getFirstLocation(),left.toString(),right.toString())
    }

    override fun infer() {
        val typesList = listOf(SyntaxType.METHOD, SyntaxType.LIST, SyntaxType.METHOD_CALL, SyntaxType.TYPE)

        if(statement.type in typesList)
        {
            val inferrer = InferrerService(InferrerDetails(
                statement,variables, CompilationExceptionsCollection(),additionalVars))
            inferrer.infer()
            exceptions.addAll(inferrer.exceptions)
        }
        else if(statement.components.size==1 && statement.components[0].type in typesList)
        {
            val inferrer = InferrerService(
                InferrerDetails(statement.components[0],
                variables, CompilationExceptionsCollection(), additionalVars)
            )
            inferrer.infer()
            when (statement.components[0].type) {
                SyntaxType.LIST -> {
                    //set list type
                    val newListType = JSONStatement(type = SyntaxType.TYPE,
                        name = LIST_ID,
                        components = mutableListOf(statement.components[0].components[0]))
                    statement.components.add(0, newListType)
                }
                SyntaxType.METHOD -> {
                    val newType = statement.components[0].copyRemovingLocation()
                    newType.components.removeLast()
                    statement.components.add(0, JSONStatement(type = SyntaxType.TYPE,
                        name = METHOD_ID,
                        components = mutableListOf(newType)))
                }
                else -> throw InternalErrorException("Unknown type")
            }
            exceptions.addAll(inferrer.exceptions)
        }
        else if(statement.components.size==1 && statement.components[0].type == SyntaxType.TOKEN)
        {
            val type = inferTypeFromLiteral(statement.components[0],variables)
            statement.components.add(0,type)
        }
        else if (statement.components.size == 2 || statement.components.size == 3)
        {
            if(statement.components.first().type == SyntaxType.TYPE)
                return
            assert(statement.type == SyntaxType.EXPRESSION)

            val isThree = statement.components.size == 3

            val left = if(isThree) statement.components[0] else NOTHING_TOKEN
            val middle = if(isThree) statement.components[1] else statement.components[0]
            val right = if(isThree) statement.components[2] else statement.components[1]

            val validWithZeroComponentsTypesList = listOf(SyntaxType.TYPE, SyntaxType.TOKEN, SyntaxType.METHOD_CALL)
            assert(left.components.size > 0 || left.type in validWithZeroComponentsTypesList)
            assert(right.components.size > 0 || right.type in validWithZeroComponentsTypesList)

            //Check if value can be assigned to
            if(middle.name == ASSIGN_ID)
            {
                //Change value of variable
                if(left.components.size == 1 && IS_IDENTIFIER.test(left.components[0].name)) {
                    val variableName = left.components[0].name
                    if(variables[variableName]?.modifiable != true && variables[variableName]?.initialized != false)
                        throw CannotModifyException(left.getFirstLocation())
                    variables[variableName]?.initialized  = true
                }
                else throw ExpectedIdentifierException(left.getFirstLocation())
            }

            val leftIsToken = left.type == SyntaxType.TOKEN
            val rightIsToken = right.type == SyntaxType.TOKEN

            if(!leftIsToken)
                InferFromExpression(InferrerDetails(left, variables, exceptions, additionalVars)).infer()

            val leftType = if(leftIsToken) inferTypeFromLiteral(left,variables)
                else if(left.type == SyntaxType.EXPRESSION) left.components[0]
                else if(left.type == SyntaxType.LIST) makeList(left.components[0])
                else throw InternalErrorException()

            //Transform access operation into apply operation
            if(middle == TypeConstants.ACCESS_OPERATION)
            {
                var methodCallComponents = mutableListOf<JSONStatement>()
                if(right.type!=SyntaxType.TOKEN) {
                    if(right.type == SyntaxType.EXPRESSION && right.components.getOrNull(1)?.name == APPLY_ID) {
                        methodCallComponents = right.components[2].components
                        right.name = AccessOperationIdentifiers.get(right.components[0].name,leftType)
                            ?: right.components[0].name
                        right.type = right.components[0].type
                        right.components = right.components[0].components
                    }
                }
                else right.name = AccessOperationIdentifiers.get(right.name,leftType) ?: right.name

                //create correct components
                assert(isThree)
                middle.name = APPLY_ID
                val oldLeft = if(statement.components[0].type==SyntaxType.EXPRESSION)
                    statement.components[0]
                else JSONStatement(type = SyntaxType.EXPRESSION,
                    components = mutableListOf(statement.components[0]))
                statement.components[0] = statement.components[2]
                methodCallComponents.add(0,oldLeft)
                statement.components[2] = JSONStatement(type = SyntaxType.METHOD_CALL,
                    components = methodCallComponents)

                //redo infer
                infer()
                return
            }

            if(!rightIsToken)
                InferFromExpression(InferrerDetails(right, variables, exceptions, additionalVars)).infer()

            val middleIsImport = middle.name == IMPORT_ID

            val rightType = if(rightIsToken && !middleIsImport) inferTypeFromLiteral(right,variables)
                else if(rightIsToken && IS_IDENTIFIER.test(right.name)) inferTypeFromLiteral(right, additionalVars)
                else if(middleIsImport) throw ExpectedIdentifierException(right.getFirstLocation())
                else if(right.type == SyntaxType.EXPRESSION) right.components[0]
                else if(right.type == SyntaxType.LIST) makeList(right.components[0])
                else if(right.type == SyntaxType.METHOD) makeMethod(right)
                else if(right.type == SyntaxType.METHOD_CALL) right.components[0]
                else throw InternalErrorException()

            statement.components.add(0,getTypeOfExpression(leftType,middle,rightType))

            // simplify format
            if(right.type == SyntaxType.METHOD_CALL)
            {
                right.components = right.components[0].components[0].components
            }

            middle.name = operationName
        }
        else if(statement.components.size==4)
            return
        else throw InternalErrorException("Irregular statement found.")
    }

    companion object {
        val VALID_NAMED = arrayListOf(
            METHOD_ID,DOUBLE_ID, INT64_ID, STRING_ID, BOOLEAN_ID, LIST_ID, ANYTHING_ID, NOTHING_ID, METHOD_CALL_ID)
    }
}
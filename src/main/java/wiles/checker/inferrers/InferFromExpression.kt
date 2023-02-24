package wiles.checker.inferrers

import wiles.checker.*
import wiles.checker.CheckerConstants.NOTHING_TOKEN
import wiles.checker.InferrerUtils.inferTypeFromLiteral
import wiles.checker.InferrerUtils.makeList
import wiles.checker.SimpleTypeGenerator.getSimpleTypes
import wiles.checker.exceptions.CannotCallMethodException
import wiles.checker.exceptions.CannotModifyException
import wiles.checker.exceptions.UnknownIdentifierException
import wiles.checker.exceptions.WrongOperationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.APPLY_ID
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.LIST_ID
import wiles.shared.constants.Types.METHOD_CALL_ID

class InferFromExpression(private val details: InferrerDetails) : InferFromStatement(details) {

    private lateinit var operationName : String

    private fun createComponents(statement : JSONStatement, middleName : String): List<JSONStatement> {
        var newComponents = listOf(statement)
        val flattenTypes = listOf(EITHER_ID)
        if(middleName != ASSIGN_ID) {
            if(statement.name in flattenTypes) {
                newComponents = mutableListOf()
                for (component in statement.components) {
                    if(component.name !in flattenTypes)
                        addIfNecessary(newComponents, component)
                    else for(subComponents in createComponents(component,middleName))
                        addIfNecessary(newComponents, subComponents)
                }
            }
        }
        return newComponents
    }

    private fun unbox(statement: JSONStatement) : JSONStatement
    {
        if(statement.name == MUTABLE_ID)
            return unbox(statement.components[0])
        return statement
    }

    private fun getTypeOfExpression(left : JSONStatement, middle : JSONStatement, right: JSONStatement) : JSONStatement
    {
        assert(left.type == SyntaxType.TYPE)
        assert(middle.type == SyntaxType.TOKEN)
        assert(right.type == SyntaxType.TYPE)

        if(middle == CheckerConstants.IS_OPERATION)
            if(InferrerUtils.isFormerSuperTypeOfLatter(left, right)) {
                operationName="ANYTHING|IS|ANYTHING"
                return CheckerConstants.BOOLEAN_TYPE
            }

        val leftComponents = createComponents(left,middle.name)

        val rightComponents = createComponents(right,middle.name)

        val resultingTypes : MutableList<JSONStatement> = mutableListOf()
        var isValid = true
        for(newLeft in leftComponents)
        {
            for(newRight in rightComponents)
            {
                val triple = Triple(newLeft, middle, newRight)
                val type = getSimpleTypes(triple)
                if(type != null) {
                    addIfNecessary(resultingTypes,type)
                }
                else isValid = false
            }
        }
        if(!isValid)
            throw WrongOperationException(middle.location!!,left.toString(),right.toString())
        if(resultingTypes.isNotEmpty())
        {
            val leftText : String = if(leftComponents.size == 1) unbox(leftComponents[0]).name else ANYTHING_ID
            val rightText : String = if(rightComponents.size == 1) unbox(rightComponents[0]).name else ANYTHING_ID
            operationName = "${leftText}|${middle.name}|${rightText}"
            return if(resultingTypes.size == 1)
                resultingTypes[0]
            else JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE, components = resultingTypes)
        }
        //TODO other types
        throw WrongOperationException(middle.location!!,left.toString(),right.toString())
    }

    private fun addIfNecessary(typeList: MutableList<JSONStatement>, type: JSONStatement) {
        var alreadyExists = false
        for(alreadyExistingType in typeList)
            if(InferrerUtils.isFormerSuperTypeOfLatter(alreadyExistingType,type))
                alreadyExists = true
        if(!alreadyExists)
            typeList.add(type)
    }

    override fun infer() {
        val typesList = listOf(SyntaxType.METHOD, SyntaxType.LIST, SyntaxType.WHEN_EXPRESSION,
                SyntaxType.METHOD_CALL, SyntaxType.TYPE)

        if(statement.type in typesList)
        {
            val inferrer = Inferrer(details)
            inferrer.infer()
            exceptions.addAll(inferrer.exceptions)
        }
        else if(statement.components.size==1 && statement.components[0].type in typesList)
        {
            val inferrer = Inferrer(InferrerDetails(statement.components[0],
                variables, CompilationExceptionsCollection()))
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
            assert(statement.type == SyntaxType.EXPRESSION)

            val isThree = statement.components.size == 3

            val left = if(isThree) statement.components[0] else NOTHING_TOKEN
            val middle = if(isThree) statement.components[1] else statement.components[0]
            val right = if(isThree) statement.components[2] else statement.components[1]

            val validWithZeroComponentsTypesList = listOf(SyntaxType.TYPE, SyntaxType.TOKEN)
            assert(left.components.size > 0 || left.type in validWithZeroComponentsTypesList)
            assert(right.components.size > 0 || right.type in validWithZeroComponentsTypesList)

            //Check if value can be assigned to
            if(middle.name == ASSIGN_ID)
            {
                //Change value of variable
                if(left.components.size == 1) {
                    val variableName = left.components[0].name
                    if(variables[variableName]?.modifiable != true && variables[variableName]?.initialized != false)
                        throw CannotModifyException(left.getFirstLocation())
                    variables[variableName]?.initialized  = true
                }
                else throw CannotModifyException(left.getFirstLocation())
            }

            val leftIsToken = left.type == SyntaxType.TOKEN
            val rightIsToken = right.type == SyntaxType.TOKEN

            if(!leftIsToken)
                InferFromExpression(InferrerDetails(left, variables, exceptions)).infer()
            if(!rightIsToken)
                InferFromExpression(InferrerDetails(right, variables, exceptions)).infer()

            val leftType = if(leftIsToken) inferTypeFromLiteral(left,variables)
                else if(left.type != SyntaxType.LIST) left.components.getOrNull(0) ?: left
                else makeList(right.components[0])

            //Transform access operation into apply operation
            if(middle == CheckerConstants.ACCESS_OPERATION)
            {
                if(right.type!=SyntaxType.TOKEN)
                    throw UnknownIdentifierException(right.getFirstLocation())
                right.name=CompatibleAccess.get(right.name,leftType) ?:
                    throw UnknownIdentifierException(right.getFirstLocation())

                //create correct components
                assert(isThree)
                middle.name = APPLY_ID
                val oldLeft = if(statement.components[0].type==SyntaxType.EXPRESSION)
                    statement.components[0]
                else JSONStatement(type = SyntaxType.EXPRESSION,
                    components = mutableListOf(statement.components[0]))
                statement.components[0] = statement.components[2]
                statement.components[2] = JSONStatement(type = SyntaxType.METHOD_CALL,
                    components = mutableListOf(oldLeft))

                //redo infer
                infer()
                return
            }

            val rightType = if(rightIsToken) inferTypeFromLiteral(right,variables)
                else if(right.type != SyntaxType.LIST) right.components.getOrNull(0) ?: right
                else makeList(right.components[0])

            if(middle == CheckerConstants.APPLY_OPERATION) {
                assert(rightType.name == METHOD_CALL_ID)
                val result = InferrerUtils.getFunctionArguments(leftType, rightType)
                if(result!=null) {
                    statement.components.add(0, leftType.components[0].components[0])
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

                    operationName= METHOD_ID + APPLY_ID + METHOD_CALL_ID

                    //lmao
                    right.components[0].components[0].components = newResult
                }
                else throw CannotCallMethodException(middle.location!!)
            }
            else statement.components.add(0,getTypeOfExpression(leftType,middle,rightType))
            middle.name = operationName
        }
        else if(statement.components.size==4)
            return
        else throw InternalErrorException("Irregular statement found.")
    }
}
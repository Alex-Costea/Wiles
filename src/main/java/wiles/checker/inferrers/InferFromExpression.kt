package wiles.checker.inferrers

import wiles.checker.CheckerConstants.NOTHING_TOKEN
import wiles.checker.Inferrer
import wiles.checker.InferrerDetails
import wiles.checker.InferrerUtils.inferTypeFromLiteral
import wiles.checker.InferrerUtils.normalizeType
import wiles.checker.SimpleTypeGenerator.getSimpleTypes
import wiles.checker.exceptions.WrongOperationException
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Types.ANYTHING_ID
import wiles.shared.constants.Types.EITHER_ID

class InferFromExpression(details: InferrerDetails) : InferFromStatement(details) {

    private lateinit var operationName : String

    private fun getTypeOfExpression(left : JSONStatement, middle : JSONStatement, right: JSONStatement) : JSONStatement
    {
        assert(left.type == SyntaxType.TYPE)
        assert(middle.type == SyntaxType.TOKEN)
        assert(right.type == SyntaxType.TYPE)

        val leftComponents : List<JSONStatement> =
            if(left.name == EITHER_ID) left.components.toList() else listOf(left)
        val rightComponents : List<JSONStatement> =
            if(right.name == EITHER_ID) right.components.toList() else listOf(right)
        val resultingTypesSet : MutableSet<JSONStatement> = mutableSetOf()
        var isValid = true
        for(newLeft in leftComponents)
        {
            for(newRight in rightComponents)
            {
                val triple = Triple(newLeft,middle, newRight)
                val type = getSimpleTypes(triple)
                if(type != null)
                    resultingTypesSet.add(type)
                else isValid = false
            }
        }
        if(!isValid)
            throw WrongOperationException(middle.location!!,left.toString(),right.toString())
        if(resultingTypesSet.isNotEmpty())
        {
            val leftText : String = if(leftComponents.size == 1) left.name else ANYTHING_ID
            val rightText : String = if(rightComponents.size == 1) right.name else ANYTHING_ID
            operationName = "${leftText}|${middle.name}|${rightText}"
            val unNormalizedType = JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE,
                components = resultingTypesSet.toMutableList())
            return normalizeType(unNormalizedType)!!
        }
        //TODO other types
        throw WrongOperationException(middle.location!!,left.toString(),right.toString())
    }

    override fun infer() {
        val typesList = listOf(SyntaxType.METHOD, SyntaxType.LIST, SyntaxType.WHEN_EXPRESSION,
                SyntaxType.METHOD_CALL, SyntaxType.TYPE)

        if(statement.components.size==1 && statement.components[0].type == SyntaxType.TOKEN)
        {
            val type = inferTypeFromLiteral(statement.components[0],variables)
            statement.components.add(0,type)
        }
        else if(statement.components.size==1 && statement.components[0].type in typesList)
        {
            val inferrer = Inferrer(statement.components[0], variables)
            inferrer.infer()
            exceptions.addAll(inferrer.exceptions)
        }
        else if (statement.components.size == 2 || statement.components.size == 3)
        {
            assert(statement.type == SyntaxType.EXPRESSION)
            
            val isThree = statement.components.size == 3

            val left = if(isThree) statement.components[0] else NOTHING_TOKEN
            val middle = if(isThree) statement.components[1] else statement.components[0]
            val right = if(isThree) statement.components[2] else statement.components[1]

            val leftIsToken = left.type == SyntaxType.TOKEN
            val rightIsToken = right.type == SyntaxType.TOKEN

            if(!leftIsToken)
                InferFromExpression(InferrerDetails(left, variables, exceptions)).infer()
            if(!rightIsToken)
                InferFromExpression(InferrerDetails(right, variables, exceptions)).infer()

            val leftType = if(leftIsToken) inferTypeFromLiteral(left,variables) else left.components[0]
            val rightType = if(rightIsToken) inferTypeFromLiteral(right,variables) else right.components[0]

            statement.components.add(0,getTypeOfExpression(leftType,middle,rightType))
            middle.name = operationName
        }
        else throw InternalErrorException("Statement with irregular number of components found.")
    }
}
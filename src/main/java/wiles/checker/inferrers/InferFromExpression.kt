package wiles.checker.inferrers

import wiles.checker.InferrerDetails
import wiles.checker.InferrerUtils.inferTypeFromLiteral
import wiles.checker.InferrerUtils.normalizeType
import wiles.checker.SimpleTypeGenerator.getSimpleTypes
import wiles.checker.exceptions.WrongOperationException
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

class InferFromExpression(details: InferrerDetails) : InferFromStatement(details) {

    private lateinit var operationName : String

    private fun getTypeOfExpression(left : JSONStatement?, middle : JSONStatement, right: JSONStatement) : JSONStatement
    {
        if(left != null)
            assert(left.type == SyntaxType.TYPE)
        assert(middle.type == SyntaxType.TOKEN)
        assert(right.type == SyntaxType.TYPE)
        val triple = Triple(normalizeType(left),middle,normalizeType(right))
        val type = getSimpleTypes[triple]
        if(type !=null) {
            operationName = "${left?.name?:""}|${middle.name}|${right.name}"
            return type
        }
        //TODO complex types
        throw WrongOperationException(middle.location!!,left.toString(),right.toString())
    }

    override fun infer() {
        if(statement.components.size==1 && statement.components[0].type == SyntaxType.TOKEN)
        {
            val type = inferTypeFromLiteral(statement.components[0],variables)
            statement.components.add(0,type)
        }
        else if (statement.components.size == 2 || statement.components.size == 3)
        {
            val isThree = statement.components.size == 3

            val left = if(isThree) statement.components[0] else null
            val middle = if(isThree) statement.components[1] else statement.components[0]
            val right = if(isThree) statement.components[2] else statement.components[1]

            val leftIsToken = left?.type == SyntaxType.TOKEN
            val rightIsToken = right.type == SyntaxType.TOKEN

            if(left != null && !leftIsToken)
                InferFromExpression(InferrerDetails(left, variables, exceptions)).infer()
            if(!rightIsToken)
                InferFromExpression(InferrerDetails(right, variables, exceptions)).infer()

            val leftType = if(leftIsToken) inferTypeFromLiteral(left!!,variables) else left?.components?.get(0)
            val rightType = if(rightIsToken) inferTypeFromLiteral(right,variables) else right.components[0]

            statement.components.add(0,getTypeOfExpression(leftType,middle,rightType))
            middle.name = operationName
        }
        else throw InternalErrorException("Statement with irregular number of components found.")
    }
}
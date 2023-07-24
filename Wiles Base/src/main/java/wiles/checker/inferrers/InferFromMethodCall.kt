package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.ExpectedIdentifierException
import wiles.shared.SyntaxType
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ASSIGN_ID
import wiles.shared.constants.TypeConstants.METHOD_CALL_TYPE

class InferFromMethodCall(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        for(expression in statement.components)
        {
            var isAssignment = false
            val expressionToInfer =
                if(expression.components.size>=2 && expression.components[1].name == ASSIGN_ID) {
                    isAssignment = true
                    expression.components[2]
            } else expression
            if(isAssignment) {
                val identifierExpression = expression.components[0].components[0]
                if (expression.components[0].components.size != 1 || !IS_IDENTIFIER.test(identifierExpression.name))
                    throw ExpectedIdentifierException(identifierExpression.getFirstLocation())
                expression.components[0] = identifierExpression
            }
            if(expression.components[0].syntaxType==SyntaxType.TYPE)
                continue
            InferFromExpression(InferrerDetails(expressionToInfer,variables, exceptions, additionalVars, context)).infer()
        }
        val type = METHOD_CALL_TYPE.copyRemovingLocation()
        type.components.add(statement.copyRemovingLocation())
        statement.components.clear()
        statement.components.add(type)
    }
}
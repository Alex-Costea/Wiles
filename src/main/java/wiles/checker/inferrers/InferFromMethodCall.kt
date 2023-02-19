package wiles.checker.inferrers

import wiles.checker.CheckerConstants.METHOD_CALL_TYPE
import wiles.checker.InferrerDetails
import wiles.shared.constants.Tokens.ASSIGN_ID

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
            if(isAssignment)
                expression.components[0]=expression.components[0].components[0]
            InferFromExpression(InferrerDetails(expressionToInfer,variables, exceptions)).infer()
        }
        val type = METHOD_CALL_TYPE.copyRemovingLocation()
        type.components.add(statement.copyRemovingLocation())
        statement.components.clear()
        statement.components.add(type)
    }
}
package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.statics.CheckerConstants.METHOD_CALL_TYPE
import wiles.shared.SyntaxType
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
            if(expression.components[0].type==SyntaxType.TYPE)
                continue
            InferFromExpression(InferrerDetails(expressionToInfer,variables, exceptions, additionalVars)).infer()
        }
        val type = METHOD_CALL_TYPE.copyRemovingLocation()
        type.components.add(statement.copyRemovingLocation())
        statement.components.clear()
        statement.components.add(type)
    }
}
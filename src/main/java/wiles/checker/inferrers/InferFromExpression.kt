package wiles.checker.inferrers

import wiles.checker.InferrerDetails
import wiles.checker.InferrerUtils
import wiles.shared.SyntaxType

class InferFromExpression(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        if(statement.components.size==1 && statement.components[0].type == SyntaxType.TOKEN)
        {
            val type = InferrerUtils.inferTypeFromLiteral(statement.components[0],variables)
            statement.components.add(0,type)
        }
        else TODO()
    }
}
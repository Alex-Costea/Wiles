package wiles.checker.inferrers

import wiles.checker.InferrerDetails
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.METHOD_ID

class InferFromType(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        if(statement.name==METHOD_ID)
        {
            val method = statement.components[0]
            for(component in method.components)
            {
                if(component.type == SyntaxType.DECLARATION)
                {
                    InferFromDeclaration(InferrerDetails(component, variables.copy(), exceptions)).infer()
                }
            }
        }
    }
}
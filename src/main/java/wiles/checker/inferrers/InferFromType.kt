package wiles.checker.inferrers

import wiles.checker.CheckerConstants.NOTHING_TYPE
import wiles.checker.InferrerDetails
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.Types.EITHER_ID

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
            method.components.add(0,NOTHING_TYPE)
        }
        else if(statement.name == EITHER_ID || statement.name == MUTABLE_ID)
        {
            for(component in statement.components)
            {
                InferFromType(InferrerDetails(component,variables,exceptions)).infer()
            }
        }
    }
}
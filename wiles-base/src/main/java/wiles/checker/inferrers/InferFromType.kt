package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.statics.InferrerUtils.createTypes
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.Types.REQUIRES_SUBTYPE

class InferFromType(details: InferrerDetails)
    : InferFromStatement(details) {
    override fun infer() {
        if(statement.name==METHOD_ID)
        {
            val method = statement.components[0]
            for(component in method.components)
            {
                if(component.syntaxType == SyntaxType.DECLARATION)
                {
                    InferFromDeclaration(
                        InferrerDetails(component, variables.copy(), exceptions)
                    ).infer()
                }
                else if(component.syntaxType == SyntaxType.TYPE)
                    InferFromType(InferrerDetails(component,variables,exceptions)).infer()
            }
            if(method.components.getOrNull(0)?.syntaxType != SyntaxType.TYPE)
                method.components.add(0,NOTHING_TYPE)
        }
        else if(statement.name in REQUIRES_SUBTYPE)
        {
            for(component in statement.components)
            {
                InferFromType(InferrerDetails(component,variables,exceptions)).infer()
            }
        }
        createTypes(statement, variables)
    }
}
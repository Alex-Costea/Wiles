package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.statics.InferrerUtils.createGenericType
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.GENERIC_ID
import wiles.shared.constants.Types.LIST_ID

class InferFromType(details: InferrerDetails,
                    private val genericTypes : MutableMap<String,JSONStatement> = mutableMapOf(),
                    private val isTopMostType : Boolean = true)
    : InferFromStatement(details) {
    override fun infer() {
        if(statement.name == GENERIC_ID)
        {
            InferFromType(InferrerDetails(statement.components[1],variables,exceptions, additionalVars),
                genericTypes,false).infer()
            genericTypes[statement.components[0].name] = statement.components[1]
        }

        if(statement.name==METHOD_ID)
        {
            val method = statement.components[0]
            for(component in method.components)
            {
                if(component.type == SyntaxType.DECLARATION)
                {
                    InferFromDeclaration(InferrerDetails(component, variables.copy(), exceptions, additionalVars),
                        genericTypes = genericTypes, isTopMostType = false).infer()
                }
            }
            if(method.components.getOrNull(0)?.type != SyntaxType.TYPE)
                method.components.add(0,NOTHING_TYPE)
        }
        else if(statement.name == EITHER_ID || statement.name == MUTABLE_ID || statement.name == LIST_ID)
        {
            for(component in statement.components)
            {
                InferFromType(InferrerDetails(component,variables,exceptions, additionalVars), genericTypes).infer()
            }
        }
        if(isTopMostType)
            createGenericType(statement,genericTypes)
        //TODO: add T to variables
    }
}
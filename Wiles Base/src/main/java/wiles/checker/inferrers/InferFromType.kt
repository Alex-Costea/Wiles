package wiles.checker.inferrers

import wiles.checker.Checker
import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.GenericAlreadyDefinedException
import wiles.checker.statics.InferrerUtils.createTypes
import wiles.checker.statics.InferrerUtils.getTypeNumber
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.METHOD_ID
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.Types.GENERIC_ID
import wiles.shared.constants.Types.REQUIRES_SUBTYPE

class InferFromType(details: InferrerDetails,
                    private val genericTypes : MutableMap<String,JSONStatement> = mutableMapOf(),
                    private val isTopMostType : Boolean = false)
    : InferFromStatement(details) {
    override fun infer() {
        if(statement.name == GENERIC_ID)
        {
            InferFromType(InferrerDetails(statement.components[1],variables,exceptions, additionalVars),
                genericTypes,false).infer()
            val name = getTypeNumber(statement.components[0].name)
            if(genericTypes.containsKey(name))
                throw GenericAlreadyDefinedException(statement.getFirstLocation())
            genericTypes[name] = statement.components[1]
        }

        if(statement.name==METHOD_ID)
        {
            if(isTopMostType)
                Checker.currentFunctionNumber++
            val method = statement.components[0]
            for(component in method.components)
            {
                if(component.syntaxType == SyntaxType.DECLARATION)
                {
                    InferFromDeclaration(InferrerDetails(component, variables.copy(), exceptions, additionalVars),
                        genericTypes = genericTypes, isTopMostType = false).infer()
                }
                else if(component.syntaxType == SyntaxType.TYPE)
                    InferFromType(InferrerDetails(component,variables,exceptions, additionalVars), genericTypes).infer()
            }
            if(method.components.getOrNull(0)?.syntaxType != SyntaxType.TYPE)
                method.components.add(0,NOTHING_TYPE)
        }
        else if(statement.name in REQUIRES_SUBTYPE)
        {
            for(component in statement.components)
            {
                InferFromType(InferrerDetails(component,variables,exceptions, additionalVars), genericTypes).infer()
            }
        }
        if(isTopMostType) {
            createTypes(statement, genericTypes, variables)
        }
    }
}
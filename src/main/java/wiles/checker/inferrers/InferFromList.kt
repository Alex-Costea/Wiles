package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.InferenceFailException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.TypeConstants.isFormerSuperTypeOfLatter

class InferFromList(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        assert(statement.type==SyntaxType.LIST)
        val statedType = if(statement.components.isNotEmpty() && statement.components[0].type==SyntaxType.TYPE)
            statement.components[0] else null
        var inferredType : JSONStatement? = null
        for(component in statement.components)
        {
            if(component.type==SyntaxType.TYPE)
                continue
            assert(component.type==SyntaxType.EXPRESSION)
            val inferrer = InferFromExpression(
                InferrerDetails(component, variables, CompilationExceptionsCollection(), additionalVars)
            )
            inferrer.infer()
            val newType = component.components[0]
            exceptions.addAll(inferrer.exceptions)

            inferredType = if(inferredType==null || isFormerSuperTypeOfLatter(newType,inferredType))
                newType
            else if(isFormerSuperTypeOfLatter(inferredType,newType))
            {
                continue
            }
            else{
                if(statedType!=null)
                {
                    if(isFormerSuperTypeOfLatter(statedType,newType)) {
                        inferredType = statedType
                        continue
                    }
                    throw ConflictingTypeDefinitionException(component.getFirstLocation(),
                        statedType.toString(),newType.toString())
                }
                throw InferenceFailException(component.getFirstLocation())
            }
        }
        if(statedType!=null) {
            if(inferredType!=null) {
                if (!isFormerSuperTypeOfLatter(statedType, inferredType))
                    throw ConflictingTypeDefinitionException(statement.components[0].getFirstLocation(),
                        statedType.toString(),inferredType.toString())
            }
        }
        else if(inferredType!=null) {
            statement.components.add(0, inferredType)
        }
        else throw InferenceFailException(statement.getFirstLocation())
    }
}
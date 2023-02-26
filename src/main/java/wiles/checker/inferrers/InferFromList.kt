package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.InferenceFailException
import wiles.checker.statics.InferrerUtils
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

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

            inferredType = if(inferredType==null || InferrerUtils.isFormerSuperTypeOfLatter(newType,inferredType))
                newType
            else if(InferrerUtils.isFormerSuperTypeOfLatter(inferredType,newType))
            {
                continue
            }
            else{
                if(statedType!=null)
                {
                    if(InferrerUtils.isFormerSuperTypeOfLatter(statedType,newType))
                        inferredType=statedType
                    continue
                }
                throw InferenceFailException(component.getFirstLocation())
            }
        }
        if(statedType!=null) {
            if(inferredType!=null) {
                if (!InferrerUtils.isFormerSuperTypeOfLatter(statedType, inferredType))
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
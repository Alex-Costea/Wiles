package wiles.checker.inferrers

import wiles.checker.statics.CheckerConstants.NOTHING_TYPE
import wiles.checker.services.Inferrer
import wiles.checker.data.InferrerDetails
import wiles.checker.statics.InferrerUtils
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.InferenceFailException
import wiles.checker.exceptions.ReturnNotGuaranteedException
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType

class InferFromMethod(details: InferrerDetails) : InferFromStatement(
    InferrerDetails(details.statement,details.variables.copy(),details.exceptions)
)
{
    private val statedType = if(statement.components.getOrNull(0)?.type == SyntaxType.TYPE)
        statement.components[0]
    else null

    private var inferredType : JSONStatement? = null

    private fun handleReturnValue(returnStatement: JSONStatement) {
        val statement=returnStatement.components[0]
        val newType = statement.components[0]
        val inferredType = inferredType

        this.inferredType = if (inferredType == null || InferrerUtils.isFormerSuperTypeOfLatter(newType, inferredType))
            newType
        else if (InferrerUtils.isFormerSuperTypeOfLatter(inferredType, newType))
            return
        else if (statedType != null) {
            if (InferrerUtils.isFormerSuperTypeOfLatter(statedType, newType))
                statedType
            else throw ConflictingTypeDefinitionException(statement.getFirstLocation(),
                statedType.toString(),newType.toString())
            }
        else throw InferenceFailException(statement.getFirstLocation())
    }

    private fun checkAlwaysReturns(statement: JSONStatement) : Boolean
    {
        for(component in statement.components.reversed())
        {
            if(component.type==SyntaxType.METHOD)
                continue
            if(component.type==SyntaxType.RETURN)
                return true
            if(component.type==SyntaxType.WHEN)
            {
                var alwaysReturns = true
                for(whenComponent in component.components)
                {
                    if(whenComponent.type!=SyntaxType.CODE_BLOCK)
                        continue
                    alwaysReturns = alwaysReturns && checkAlwaysReturns(whenComponent)
                }
                if(alwaysReturns)
                    return true
            }
        }
        return false
    }

    private fun findReturnPoints(statement: JSONStatement)
    {
        for(component in statement.components)
        {
            if(component.type==SyntaxType.METHOD)
                continue
            if(component.type == SyntaxType.RETURN)
                handleReturnValue(component)
            findReturnPoints(component)
        }
    }

    override fun infer() {
        for(component in statement.components)
        {
            if(component.type==SyntaxType.TYPE)
                continue
            if(component.type==SyntaxType.CODE_BLOCK)
                break
            assert(component.type == SyntaxType.DECLARATION)

            val inferrer = InferFromDeclaration(InferrerDetails(component, variables, exceptions), alwaysInit = true)
            inferrer.infer()
        }

        val inferrer = Inferrer(InferrerDetails(statement.components.last(),variables, exceptions))
        inferrer.infer()

        findReturnPoints(statement.components.last())

        val inferredType = inferredType
        if(statedType!=null) {
            if(inferredType!=null) {
                if (!InferrerUtils.isFormerSuperTypeOfLatter(statedType, inferredType))
                    throw ConflictingTypeDefinitionException(statement.components[0].getFirstLocation(),
                        statedType.toString(),inferredType.toString())
            }
        }
        else statement.components.add(0, inferredType?:NOTHING_TYPE)

        if(inferredType!=null || statedType!=null)
            if(!checkAlwaysReturns(statement.components.last()))
                throw ReturnNotGuaranteedException(statement.getFirstLocation())
    }
}
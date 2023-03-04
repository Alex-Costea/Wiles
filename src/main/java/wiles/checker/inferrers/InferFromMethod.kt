package wiles.checker.inferrers

import wiles.checker.Checker
import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableMap
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.InferenceFailException
import wiles.checker.exceptions.ReturnNotGuaranteedException
import wiles.checker.services.InferrerService
import wiles.shared.constants.CheckerConstants.NOTHING_TYPE
import wiles.checker.statics.InferrerUtils
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Tokens.ELSE_ID

class InferFromMethod(details: InferrerDetails) : InferFromStatement(
    InferrerDetails(details.statement,
        Checker.getVariables(),
        details.exceptions,
        additionalVars = details.variables.copy())
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
            if(component.type==SyntaxType.IF || component.type == SyntaxType.WHEN)
            {
                var alwaysReturns = true
                var hasLast = false
                for(ifComponent in component.components)
                {
                    if(ifComponent.name == ELSE_ID)
                        hasLast = true
                    if(ifComponent.type!=SyntaxType.CODE_BLOCK)
                        continue
                    alwaysReturns = alwaysReturns && checkAlwaysReturns(ifComponent)
                }
                if(hasLast && alwaysReturns)
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
        val declarationVariables = additionalVars.copy()
        for(component in statement.components)
        {
            if(component.type==SyntaxType.TYPE)
                continue
            if(component.type==SyntaxType.CODE_BLOCK)
                break
            assert(component.type == SyntaxType.DECLARATION)

            val inferrer = InferFromDeclaration(InferrerDetails(component, declarationVariables, exceptions, VariableMap()), alwaysInit = true)
            inferrer.infer()
        }

        variables.putAll(declarationVariables.filter { it.key !in additionalVars })

        val inferrer = InferrerService(InferrerDetails(statement.components.last(), variables, exceptions, additionalVars))
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
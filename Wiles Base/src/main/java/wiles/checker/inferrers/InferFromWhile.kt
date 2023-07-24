package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.shared.constants.TypeConstants.BOOLEAN_TYPE
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter

class InferFromWhile(details: InferrerDetails) : InferFromStatement(
    InferrerDetails(details.statement,
        details.variables.copy(),
        details.exceptions,
        details.additionalVars,
        details.context)
) {
    override fun infer() {
        val expression = statement.components[0]
        val inferFromExpression = InferFromExpression(InferrerDetails(expression,
            variables, exceptions, additionalVars,context))
        inferFromExpression.infer()

        val expressionType = statement.components[0].components[0]
        if(!isFormerSuperTypeOfLatter(BOOLEAN_TYPE,expressionType))
            throw ConflictingTypeDefinitionException(expression.getFirstLocation(),
                BOOLEAN_TYPE.toString(), expressionType.toString())

        val codeBlock = statement.components[1]
        val inferFromCodeBlock = InferFromCodeBlock(InferrerDetails(codeBlock,
            variables, exceptions, additionalVars,context))
        inferFromCodeBlock.infer()

    }
}
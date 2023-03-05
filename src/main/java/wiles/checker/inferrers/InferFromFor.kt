package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.VariableAlreadyDeclaredException
import wiles.shared.constants.TypeConstants.INT64_TYPE
import wiles.shared.constants.TypeConstants.LIST_OF_NULLABLE_ANYTHING_TYPE
import wiles.checker.statics.InferrerUtils
import wiles.checker.statics.InferrerUtils.getElementTypeFromListType
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.FROM_ID
import wiles.shared.constants.Tokens.IN_ID
import wiles.shared.constants.Tokens.TO_ID

class InferFromFor(details: InferrerDetails) : InferFromStatement(
    InferrerDetails(details.statement,
        details.variables.copy(),
        details.exceptions,
        details.additionalVars)
) {

    private fun checkCorrectType(components : MutableList<JSONStatement>, superType : JSONStatement) : JSONStatement
    {
        components.removeFirst()
        val expression = components[0]
        InferFromExpression(InferrerDetails(expression, variables, exceptions, additionalVars)).infer()
        val expressionType = components[0].components[0]

        if(!InferrerUtils.isFormerSuperTypeOfLatter(superType, expressionType))
            throw ConflictingTypeDefinitionException(expression.getFirstLocation(),
                superType.toString(), expressionType.toString())
        components.removeFirst()

        return expressionType
    }

    override fun infer() {
        val variableName = statement.components[0].name
        if(variableName in variables.keys)
            throw VariableAlreadyDeclaredException(statement.components[0].getFirstLocation())

        variables[variableName] = VariableDetails(INT64_TYPE)

        val components = statement.components.toMutableList()
        components.removeFirst()

        if(components.getOrNull(0)?.name == IN_ID)
        {
            val expressionType = checkCorrectType(components, LIST_OF_NULLABLE_ANYTHING_TYPE)
            variables[variableName] = VariableDetails(getElementTypeFromListType(expressionType))
        }

        if(components.getOrNull(0)?.name == FROM_ID)
        {
            checkCorrectType(components, INT64_TYPE)
        }

        if(components.getOrNull(0)?.name == TO_ID)
        {
            checkCorrectType(components, INT64_TYPE)
        }

        val codeBlock = components[0]
        val inferFromCodeBlock = InferFromCodeBlock(InferrerDetails(codeBlock,
            variables, exceptions, additionalVars))
        inferFromCodeBlock.infer()

        statement.components.add(0,variables[variableName]!!.type)
    }
}
package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.VariableAlreadyDeclaredException
import wiles.checker.statics.InferrerUtils.getElementTypeFromListType
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.FROM_ID
import wiles.shared.constants.Tokens.IN_ID
import wiles.shared.constants.Tokens.TO_ID
import wiles.shared.constants.TypeConstants.INT_TYPE
import wiles.shared.constants.TypeConstants.LIST_OF_NULLABLE_ANYTHING_TYPE
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter

class InferFromFor(details: InferrerDetails) : InferFromStatement(
    InferrerDetails(details.statement,
        details.variables.copy(),
        details.exceptions,
        details.context)
) {

    private fun checkCorrectType(components : MutableList<JSONStatement>, superType : JSONStatement) : JSONStatement
    {
        components.removeFirst()
        val expression = components[0]
        InferFromExpression(InferrerDetails(expression, variables, exceptions, context)).infer()
        val expressionType = components[0].components[0]

        if(!isFormerSuperTypeOfLatter(superType, expressionType))
            throw ConflictingTypeDefinitionException(expression.getFirstLocation(),
                superType.toString(), expressionType.toString())
        components.removeFirst()

        return expressionType
    }

    override fun infer() {
        val variableName = statement.components[0].name
        if(variableName in variables.keys)
            throw VariableAlreadyDeclaredException(statement.components[0].getFirstLocation())

        variables[variableName] = VariableDetails(INT_TYPE)

        val components = statement.components.toMutableList()
        components.removeFirst()

        if(components.getOrNull(0)?.name == IN_ID)
        {
            val expressionType = checkCorrectType(components, LIST_OF_NULLABLE_ANYTHING_TYPE)
            variables[variableName] = VariableDetails(getElementTypeFromListType(expressionType))
        }

        if(components.getOrNull(0)?.name == FROM_ID)
        {
            checkCorrectType(components, INT_TYPE)
        }

        if(components.getOrNull(0)?.name == TO_ID)
        {
            checkCorrectType(components, INT_TYPE)
        }

        val codeBlock = components[0]
        val inferFromCodeBlock = InferFromCodeBlock(InferrerDetails(codeBlock,
            variables, exceptions, context))
        inferFromCodeBlock.infer()

        statement.components.add(0,variables[variableName]!!.type)
    }
}
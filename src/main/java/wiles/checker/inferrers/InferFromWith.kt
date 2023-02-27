package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.ExpectedIdentifierException
import wiles.checker.exceptions.UnknownIdentifierException
import wiles.checker.statics.InferrerUtils
import wiles.shared.constants.Predicates.IS_IDENTIFIER

class InferFromWith(details: InferrerDetails) : InferFromStatement(
    InferrerDetails(
        statement = details.statement,
        variables = details.variables.copy(),
        exceptions = details.exceptions,
        additionalVars = details.additionalVars
)
) {
    override fun infer() {
        val statedType = statement.components[0]
        val expression = statement.components[1].components[0]
        val location = statement.components[1].getFirstLocation()
        val name = expression.name
        if(statement.components[1].components.size != 1 || !IS_IDENTIFIER.test(name))
            throw ExpectedIdentifierException(location)

        val variableDetails = variables[name] ?: throw UnknownIdentifierException(location)
        val inferredType = variableDetails.type

        if(!InferrerUtils.isFormerSuperTypeOfLatter(inferredType, statedType)) {
            throw ConflictingTypeDefinitionException(location, inferredType.toString(), statedType.toString())
        }

        val newVariables = variables.copy()
        newVariables[name] = VariableDetails(statedType,
            initialized = variableDetails.initialized,
            modifiable = variableDetails.modifiable)

        InferFromCodeBlock(InferrerDetails(statement.components[2],
            newVariables,exceptions,additionalVars)).infer()
    }
}
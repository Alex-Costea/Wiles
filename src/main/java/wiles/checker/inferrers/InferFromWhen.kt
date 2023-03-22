package wiles.checker.inferrers

import wiles.checker.data.CheckerVariableMap
import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.exceptions.*
import wiles.checker.statics.InferrerUtils.checkIsInitialized
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.TokenLocation
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.Types.EITHER_ID

class InferFromWhen(details: InferrerDetails) : InferFromStatement(details) {

    private fun getFormerTypeMinusLatterType(
        former: JSONStatement, latter: JSONStatement,
        newLocation: TokenLocation
    ): JSONStatement {
        if (!isFormerSuperTypeOfLatter(former, latter))
            throw ConflictingTypeDefinitionException(latter.getFirstLocation(), latter.toString(), former.toString())
        if (former.name == EITHER_ID && former.components.size == 0)
            throw TypesExhaustedException(newLocation)
        val result = former.copyRemovingLocation()
        isFormerSuperTypeOfLatter(latter, result, getMinus = true)
        return removeEmptyEither(result)
    }

    private fun removeEmptyEither(statement : JSONStatement) : JSONStatement
    {
        var i = 0
        while(i < statement.components.size) {
            val component = statement.components[i]
            if(component.name == EITHER_ID && component.components.isEmpty())
            {
                statement.components.removeAt(i)
            }
            else {
                statement.components[i] = removeEmptyEither(component)
                i++
            }
        }
        if(statement.name == EITHER_ID && statement.components.size == 1)
            return statement.components[0]
        return statement
    }

    override fun infer() {
        val expression = statement.components.first()
        val listOfVariableMaps = mutableListOf<CheckerVariableMap>()
        val codeBlockLists = mutableListOf<JSONStatement>()
        val location = expression.getFirstLocation()
        val name = expression.components[0].name
        if(expression.components.size != 1 || !IS_IDENTIFIER.test(name))
            throw ExpectedIdentifierException(location)

        val components = statement.components.toMutableList()
        components.removeFirst()

        val variableDetails = variables[name] ?: throw UnknownIdentifierException(location)
        if(!variableDetails.initialized) throw UsedBeforeInitializationException(location)
        var inferredType = variableDetails.type

        for(component in components)
        {
            if(component.type == SyntaxType.TYPE)
            {
                if (!isFormerSuperTypeOfLatter(inferredType, component)) {
                    throw ConflictingTypeDefinitionException(component.getFirstLocation(),
                        inferredType.toString(), component.toString())
                }
            }
        }

        while(components.isNotEmpty())
        {
            val newLocation = components.first().getFirstLocation()
            val statedType = if(components.first().type == SyntaxType.TYPE) components.first() else {
                components.first().components.add(inferredType)
                components.first().type = SyntaxType.TYPE
                inferredType
            }
            components.removeFirst()

            inferredType = getFormerTypeMinusLatterType(inferredType, statedType, newLocation)

            val newVariables = variables.copy()
            newVariables[name] = VariableDetails(
                type = statedType,
                initialized = variableDetails.initialized,
                modifiable = variableDetails.modifiable
            )

            val block = components.removeFirst()
            InferFromCodeBlock(InferrerDetails(block, newVariables,
                exceptions, additionalVars)).infer()
            listOfVariableMaps.add(newVariables)
            codeBlockLists.add(block)
        }

        if(inferredType.name == EITHER_ID && inferredType.components.isEmpty()) {
            val lastType = statement.components.last { it.type == SyntaxType.TYPE }
            if(lastType.components.isEmpty()) {
                val lastTypeBefore = lastType.copyRemovingLocation()
                lastType.name = ELSE_ID
                lastType.components.clear()
                lastType.components.add(lastTypeBefore)
                lastType.type = SyntaxType.TYPE
            }
        }

        checkIsInitialized(variables, listOfVariableMaps, codeBlockLists, statement.components)
    }
}

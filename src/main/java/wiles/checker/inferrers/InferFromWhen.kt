package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.data.VariableMap
import wiles.checker.exceptions.*
import wiles.checker.statics.InferrerUtils
import wiles.checker.statics.InferrerUtils.checkIsInitialized
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.TokenLocation
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.TypeConstants.isFormerSuperTypeOfLatter
import wiles.shared.constants.Types.EITHER_ID
import wiles.shared.constants.Types.LIST_ID

class InferFromWhen(details: InferrerDetails) : InferFromStatement(details) {

    private fun getFormerTypeMinusLatterType(former: JSONStatement, latter : JSONStatement,
                                             newLocation : TokenLocation) : JSONStatement
    {
        // use sub-components
        for(typeName in arrayOf(MUTABLE_ID, LIST_ID)) {
            if (former.name == typeName && latter.name == typeName) {
                return JSONStatement(
                    name = typeName, type = SyntaxType.TYPE, components = mutableListOf(
                        getFormerTypeMinusLatterType(former.components[0], latter.components[0], newLocation)))
            }
        }

        if(!isFormerSuperTypeOfLatter(former, latter))
            throw ConflictingTypeDefinitionException(latter.getFirstLocation(),latter.toString(),former.toString())

        if(former.name == EITHER_ID && former.components.size==0)
            throw TypesExhaustedException(newLocation)

        if(InferrerUtils.areTypesEquivalent(latter, former))
            return JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE)

        if(former.name == EITHER_ID)
        {
            val latterComponents = if(latter.name == EITHER_ID) {
                InferrerUtils.createComponents(latter).toList()
            } else listOf(latter)
            var components = InferrerUtils.createComponents(former).toMutableList()
            for(latterComponent in latterComponents) {
                for (formerComponent in components.withIndex()) {
                    if (isFormerSuperTypeOfLatter(formerComponent.value, latterComponent)) {
                        components[formerComponent.index] = getFormerTypeMinusLatterType(formerComponent.value,
                            latterComponent, newLocation)

                    }
                }
                components = components.filterNot { it.name == EITHER_ID && it.components.isEmpty()}.toMutableList()
            }

            return when (components.size) {
                0 -> JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE)
                1 -> components[0].copyRemovingLocation()
                else -> JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE, components = components)
            }
        }
        return former.copyRemovingLocation()
    }

    override fun infer() {
        val expression = statement.components.first()
        val listOfVariableMaps = mutableListOf<VariableMap>()
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
            components.first().getFirstLocation()
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

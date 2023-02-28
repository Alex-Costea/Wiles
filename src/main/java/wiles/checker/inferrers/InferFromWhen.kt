package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.data.VariableMap
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.ExpectedIdentifierException
import wiles.checker.exceptions.TypesExhaustedException
import wiles.checker.exceptions.UnknownIdentifierException
import wiles.checker.statics.InferrerUtils
import wiles.checker.statics.InferrerUtils.checkIsInitialized
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Tokens.ELSE_ID
import wiles.shared.constants.Types.EITHER_ID

class InferFromWhen(details: InferrerDetails) : InferFromStatement(details) {

    private fun getFormerTypeMinusLatterType(former: JSONStatement, latter : JSONStatement) : JSONStatement?
    {
        assert(InferrerUtils.isFormerSuperTypeOfLatter(former, latter))
        val unboxedFormer = InferrerUtils.unbox(former)
        val unboxedLatter = InferrerUtils.unbox(latter)

        if(unboxedFormer.name == EITHER_ID && unboxedFormer.components.size==0)
            return null

        if(InferrerUtils.areTypesEquivalent(latter, unboxedFormer))
            return JSONStatement(name = EITHER_ID, type = SyntaxType.TYPE)

        if(unboxedFormer.name == EITHER_ID)
        {
            val latterComponents = if(unboxedLatter.name == EITHER_ID) {
                unboxedLatter.components.toList()
            } else listOf(latter)
            val components = InferrerUtils.createComponents(unboxedFormer).toMutableList()
            for(latterComponent in latterComponents) {
                var i = 0
                while (i < components.size) {
                    if (InferrerUtils.isFormerSuperTypeOfLatter(components[i], latterComponent)) {
                        components.removeAt(i)
                        i--
                    }
                    i++
                }
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
        var inferredType = variableDetails.type

        for(component in components)
        {
            if(component.type == SyntaxType.TYPE)
            {
                if (!InferrerUtils.isFormerSuperTypeOfLatter(inferredType, component)) {
                    throw ConflictingTypeDefinitionException(
                        component.getFirstLocation(),
                        inferredType.toString(),
                        component.toString()
                    )
                }
            }
        }

        while(components.isNotEmpty())
        {
            components.first().getFirstLocation()
            val newLocation = components.first().getFirstLocation()
            val statedType = if(components.first().type == SyntaxType.TYPE) components.first() else inferredType
            components.removeFirst()

            inferredType = getFormerTypeMinusLatterType(inferredType, statedType)
                ?: throw TypesExhaustedException(newLocation)

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
            val lastType = statement.components.last { it.name == ELSE_ID || it.type == SyntaxType.TYPE }
            lastType.name = ELSE_ID
            lastType.components.clear()
            lastType.type = SyntaxType.TOKEN
        }

        checkIsInitialized(variables, listOfVariableMaps, codeBlockLists, statement.components)
    }
}
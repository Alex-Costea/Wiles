package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.exceptions.ExpectedIdentifierException
import wiles.checker.exceptions.TypesExhaustedException
import wiles.checker.exceptions.UnknownIdentifierException
import wiles.checker.statics.InferrerUtils
import wiles.shared.JSONStatement
import wiles.shared.SyntaxType
import wiles.shared.constants.Predicates.IS_IDENTIFIER
import wiles.shared.constants.Types

class InferFromWhen(details: InferrerDetails) : InferFromStatement(
    InferrerDetails(
        statement = details.statement,
        variables = details.variables.copy(),
        exceptions = details.exceptions,
        additionalVars = details.additionalVars
)
) {

    private fun getFormerTypeMinusLatterType(former: JSONStatement, latter : JSONStatement) : JSONStatement?
    {
        assert(InferrerUtils.isFormerSuperTypeOfLatter(former, latter))
        val unboxedFormer = InferrerUtils.unbox(former)

        if(unboxedFormer.name == Types.EITHER_ID && unboxedFormer.components.size==0)
            return null

        if(InferrerUtils.isFormerSuperTypeOfLatter(latter, unboxedFormer))
            return JSONStatement(name = Types.EITHER_ID, type = SyntaxType.TYPE)

        if(unboxedFormer.name == Types.EITHER_ID)
        {
            val components = InferrerUtils.createComponents(unboxedFormer).toMutableList()
            var i = 0
            while(i < components.size)
            {
                if(InferrerUtils.isFormerSuperTypeOfLatter(components[i], latter)) {
                    components.removeAt(i)
                    i--
                }
                i++
            }

            return when (components.size) {
                0 -> JSONStatement(name = Types.EITHER_ID, type = SyntaxType.TYPE)
                1 -> components[0].copyRemovingLocation()
                else -> JSONStatement(name = Types.EITHER_ID, type = SyntaxType.TYPE, components = components)
            }
        }
        return unboxedFormer.copyRemovingLocation()
    }

    override fun infer() {
        val expression = statement.components.first()
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
            val statedType = if(components.first().type == SyntaxType.TYPE) components.removeFirst() else inferredType

            inferredType = getFormerTypeMinusLatterType(inferredType, statedType)
                ?: throw TypesExhaustedException(newLocation)

            val newVariables = variables.copy()
            newVariables[name] = VariableDetails(
                type = statedType,
                initialized = variableDetails.initialized,
                modifiable = variableDetails.modifiable
            )

            InferFromCodeBlock(InferrerDetails(components.removeFirst(), newVariables,
                exceptions, additionalVars)).infer()
        }
    }
}
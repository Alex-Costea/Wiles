package wiles.checker.inferrers

import wiles.checker.data.CheckerVariableMap
import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.services.InferrerService
import wiles.checker.statics.InferrerUtils.checkVarsAfterConditional
import wiles.shared.JSONStatement
import wiles.shared.constants.TypeConstants.BOOLEAN_TYPE
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter

class InferFromIf(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        val components = statement.components.toMutableList()
        val listOfVariableMaps = mutableListOf<CheckerVariableMap>()
        val codeBlockLists = mutableListOf<JSONStatement>()
        while(components.isNotEmpty())
        {
            val condition = components.removeFirst()
            InferrerService(InferrerDetails(condition, variables.copy(), exceptions, additionalVars, context)).infer()

            if(condition.components.isNotEmpty() &&
                !isFormerSuperTypeOfLatter(BOOLEAN_TYPE,condition.components[0]))
            {
                throw ConflictingTypeDefinitionException(condition.getFirstLocation(),
                    BOOLEAN_TYPE.toString(),condition.components[0].toString())
            }

            val block = components.removeFirst()
            val newVars = variables.copy()
            InferFromCodeBlock(InferrerDetails(block, newVars, exceptions, additionalVars, context)).infer()
            listOfVariableMaps.add(newVars)
            codeBlockLists.add(block)
        }

        checkVarsAfterConditional(variables, listOfVariableMaps, codeBlockLists, statement.components)
    }
}
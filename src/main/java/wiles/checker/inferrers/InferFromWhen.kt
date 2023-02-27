package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.data.VariableDetails
import wiles.checker.data.VariableMap
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.services.InferrerService
import wiles.checker.statics.CheckerConstants.BOOLEAN_TYPE
import wiles.checker.statics.InferrerUtils
import wiles.shared.JSONStatement

class InferFromWhen(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        val components = statement.components.toMutableList()
        val listOfVariableMaps = mutableListOf<VariableMap>()
        val codeBlockLists = mutableListOf<JSONStatement>()
        while(components.isNotEmpty())
        {
            val condition = components.removeFirst()
            InferrerService(InferrerDetails(condition, variables.copy(), exceptions, additionalVars)).infer()

            if(condition.components.isNotEmpty() &&
                !InferrerUtils.isFormerSuperTypeOfLatter(BOOLEAN_TYPE,condition.components[0]))
            {
                throw ConflictingTypeDefinitionException(condition.getFirstLocation(),
                    BOOLEAN_TYPE.toString(),condition.components[0].toString())
            }

            val block = components.removeFirst()
            val newVars = variables.copy()
            InferFromCodeBlock(InferrerDetails(block, newVars, exceptions, additionalVars)).infer()
            listOfVariableMaps.add(newVars)
            codeBlockLists.add(block)
        }

        for(variable in variables.entries)
        {
            if(!variable.value.initialized)
            {
                var isInitialized = true
                for(map in listOfVariableMaps)
                {
                    if(!InferrerUtils.containsStopStatement(codeBlockLists.removeFirst())) {
                        if (!map[variable.key]!!.initialized) {
                            isInitialized = false
                        }
                    }
                }
                variables[variable.key]= VariableDetails(variable.value.type,isInitialized,variable.value.modifiable)
            }
        }
    }
}
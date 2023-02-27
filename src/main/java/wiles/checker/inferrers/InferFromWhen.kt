package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.ConflictingTypeDefinitionException
import wiles.checker.services.InferrerService
import wiles.checker.statics.CheckerConstants.BOOLEAN_TYPE
import wiles.checker.statics.InferrerUtils

class InferFromWhen(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        val components = statement.components.toMutableList()
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
            InferFromCodeBlock(InferrerDetails(block, variables.copy(), exceptions, additionalVars)).infer()
        }
    }
}
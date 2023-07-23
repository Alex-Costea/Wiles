package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.UnusedExpressionException
import wiles.checker.services.InferrerService
import wiles.shared.SyntaxType
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter

class InferFromCodeBlock(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer()
    {
        for(part in statement.components)
        {
            val inferrer = InferrerService(InferrerDetails(part,variables, exceptions, additionalVars))
            inferrer.infer()

            if(exceptions.isEmpty() &&
                part.syntaxType == SyntaxType.EXPRESSION && !isFormerSuperTypeOfLatter(NOTHING_TYPE, inferrer.getType()))
                throw UnusedExpressionException(part.getFirstLocation())
        }
    }
}
package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails
import wiles.checker.exceptions.UnusedExpressionException
import wiles.checker.services.InferrerService
import wiles.shared.AbstractCompilationException
import wiles.shared.SyntaxType
import wiles.shared.constants.TypeConstants.NOTHING_TYPE
import wiles.shared.constants.TypeConstants.isFormerSuperTypeOfLatter

class InferFromCodeBlock(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer()
    {
        for(part in statement.components)
        {
            try
            {
                val inferrer = InferrerService(InferrerDetails(part,variables, exceptions, additionalVars))
                inferrer.infer()
                if(part.type== SyntaxType.EXPRESSION && !isFormerSuperTypeOfLatter(NOTHING_TYPE, inferrer.getType()))
                    throw UnusedExpressionException(part.getFirstLocation())
            }
            catch (ex : AbstractCompilationException)
            {
                exceptions.add(ex)
            }
        }
    }
}
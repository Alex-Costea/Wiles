package wiles.checker.inferrers

import wiles.checker.CheckerConstants.NOTHING_TYPE
import wiles.checker.Inferrer
import wiles.checker.InferrerDetails
import wiles.checker.InferrerUtils.isSubtype
import wiles.checker.exceptions.UnusedExpressionException
import wiles.shared.AbstractCompilationException
import wiles.shared.SyntaxType

class InferFromCodeBlock(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer()
    {
        for(part in statement.components)
        {
            try
            {
                val inferrer = Inferrer(part, variables)
                inferrer.infer()
                if(part.type== SyntaxType.EXPRESSION && !isSubtype(NOTHING_TYPE, inferrer.getType()))
                    throw UnusedExpressionException(part.getFirstLocation())
            }
            catch (ex : AbstractCompilationException)
            {
                exceptions.add(ex)
            }
        }
    }
}
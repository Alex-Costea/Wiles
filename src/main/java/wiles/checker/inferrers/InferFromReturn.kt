package wiles.checker.inferrers

import wiles.checker.InferrerDetails

class InferFromReturn(details: InferrerDetails) : InferFromStatement(details) {
    override fun infer() {
        val inferrer = InferFromExpression(InferrerDetails(statement.components[0],variables, exceptions))
        inferrer.infer()
    }
}
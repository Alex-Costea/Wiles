package wiles.checker.inferrers

import wiles.checker.data.InferrerDetails

abstract class InferFromStatement(details: InferrerDetails) {
    val statement = details.statement
    val variables = details.variables
    val exceptions = details.exceptions
    val additionalVariables = details.additionalVariables
    abstract fun infer()
}
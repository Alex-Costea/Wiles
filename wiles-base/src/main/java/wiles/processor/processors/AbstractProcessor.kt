package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.shared.abstracts.AbstractSyntaxTree

abstract class AbstractProcessor(
    val syntax : AbstractSyntaxTree,
    val context : InterpreterContext
) {
    abstract fun process() : Value
}
package wiles.processor.processors

import wiles.processor.data.InterpreterContext
import wiles.shared.AbstractSyntaxTree

abstract class AbstractProcessor(
    val syntax : AbstractSyntaxTree,
    val context : InterpreterContext
) {
    abstract fun process()
}
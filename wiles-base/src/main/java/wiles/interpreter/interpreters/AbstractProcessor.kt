package wiles.interpreter.interpreters

import wiles.interpreter.InterpreterContext
import wiles.shared.AbstractSyntaxTree

abstract class AbstractProcessor(
    val syntax : AbstractSyntaxTree,
    val context : InterpreterContext
) {
    abstract fun process()
}
package wiles.interpreter.operations

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.values.Value

abstract class AbstractOperation(val left : Value, val right : Value, val context: InterpreterContext) {
    val leftType = left.getType()
    val rightType = right.getType()
    abstract fun getNewValue() : Value
}
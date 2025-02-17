package wiles.interpreter.operations

import wiles.interpreter.values.Value

abstract class AbstractOperation(val left : Value, val right : Value) {
    val leftType = left.getType()
    val rightType = right.getType()
    abstract fun getNewValue() : Value
}
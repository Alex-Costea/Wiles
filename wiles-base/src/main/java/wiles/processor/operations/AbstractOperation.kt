package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.values.Value

abstract class AbstractOperation(val left : Value, val right : Value, val context: InterpreterContext) {
    val leftType = left.getType()
    val rightType = right.getType()
    val leftObj = left.getObj()
    val rightObj = right.getObj()
    abstract fun getNewValue() : Value
}
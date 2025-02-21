package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.types.AbstractType
import wiles.processor.values.Value

abstract class AbstractOperation(val left : Value?, val right : Value, val context: InterpreterContext) {
    val leftType = left?.getType()
    val rightType = right.getType()
    val leftObj = left?.getObj()
    val rightObj = right.getObj()
    val bothKnown = left?.isKnown() == true && right.isKnown()
    abstract fun getNewValue() : Value
    protected abstract fun calculateObject() : Any?
    protected abstract fun calculateType() : AbstractType
}
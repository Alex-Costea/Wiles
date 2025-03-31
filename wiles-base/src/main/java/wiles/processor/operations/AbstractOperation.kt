package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.WilesType
import wiles.processor.utils.InterpreterUtils.getNewTypeObject

abstract class AbstractOperation(val left : Value?, val right : Value, val context: InterpreterContext) {
    val leftType = left?.getType()
    val rightType = right.getType()
    val leftObj = left?.getObj()
    val rightObj = right.getObj()
    val bothKnown = (left == null || left.isKnown()) && right.isKnown()

    open fun getNewValue(): Value {
        val newObject = calculateObject()
        val newType = calculateType()
        var value = Value(newObject, newType)
        if(bothKnown)
            value = Value(newObject, getNewTypeObject(value))
        return value
    }
    protected abstract fun calculateObject() : Any?
    protected abstract fun calculateType() : WilesType
}
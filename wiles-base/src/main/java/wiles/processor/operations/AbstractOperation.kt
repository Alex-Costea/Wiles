package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.types.AbstractType

abstract class AbstractOperation(val left : Value?, val right : Value, val context: InterpreterContext) {
    val leftType = left?.getType()
    val rightType = right.getType()
    val leftObj = left?.getObj()
    val rightObj = right.getObj()
    val bothKnown = (left == null || left.isKnown()) && right.isKnown()

    open fun getNewValue(): Value {
        val newObject = calculateObject()
        var newType = calculateType()
        if(bothKnown)
            newType = newType.exactly(newObject!!)
        return Value(VariableStatus.Const, newObject, newType, )
    }
    protected abstract fun calculateObject() : Any?
    protected abstract fun calculateType() : AbstractType
}
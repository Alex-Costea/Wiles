package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.types.AbstractType
import wiles.processor.types.EitherType
import wiles.processor.utils.InterpreterUtils.getNewTypeObject

class UnionOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        return Value(VariableStatus.Const, calculateObject(), calculateType(), )
    }

    override fun calculateObject(): Any? {
        if(!bothKnown)
            return null
        val leftSubtype = getNewTypeObject(left!!)
        val rightSubtype = getNewTypeObject(right)
        return EitherType(leftSubtype, rightSubtype) }

    override fun calculateType(): AbstractType {
        return AbstractType.TYPE_TYPE
    }
}
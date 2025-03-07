package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.enums.VariableStatus
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.ANYTHING_TYPE
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.NUMBER_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.utils.TypeUtils.isSuperType
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.shared.errors.InternalErrorException

class PlusOperation(left: Value, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {

    override fun calculateObject() : Any?
    {
        if(!bothKnown)
            return null
        return when{
            leftObj is WilesInteger && rightObj is WilesInteger -> leftObj + rightObj
            leftObj is WilesInteger && rightObj is WilesDecimal -> leftObj + rightObj
            leftObj is WilesDecimal && rightObj is WilesInteger -> leftObj + rightObj
            leftObj is WilesDecimal && rightObj is WilesDecimal -> leftObj + rightObj
            leftObj == null || rightObj == null -> InternalErrorException()
            else -> leftObj.toString() + rightObj.toString()
        }
    }

    override fun calculateType(): AbstractType {
        leftType!!
        val newType = when {
            isSuperType(INT_TYPE, leftType) && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            isSuperType(NUMBER_TYPE, leftType) && isSuperType(NUMBER_TYPE, rightType) -> DECIMAL_TYPE
            isSuperType(TEXT_TYPE, leftType) && isSuperType(ANYTHING_TYPE, rightType) -> TEXT_TYPE
            isSuperType(ANYTHING_TYPE, leftType) && isSuperType(TEXT_TYPE, rightType) -> TEXT_TYPE
            else -> TODO("Can't add these types")
        }
        return newType
    }

    override fun getNewValue(): Value {
        val newObject = calculateObject()
        var newType = calculateType()
        if(bothKnown)
            newType = newType.exactly(newObject!!)
        return Value(newObject, newType, VariableStatus.Const)
    }
}
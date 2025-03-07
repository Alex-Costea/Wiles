package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.NUMBER_TYPE
import wiles.processor.utils.TypeUtils.isSuperType
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.shared.errors.InternalErrorException
import wiles.shared.errors.WilesTypeException

class PowerOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun calculateObject() : Any?
    {
        return when{
            !bothKnown -> null
            leftObj is WilesInteger && rightObj is WilesInteger -> leftObj pow rightObj
            leftObj is WilesInteger && rightObj is WilesDecimal -> leftObj pow rightObj
            leftObj is WilesDecimal && rightObj is WilesInteger -> leftObj pow rightObj
            leftObj is WilesDecimal && rightObj is WilesDecimal -> leftObj pow rightObj
            else -> InternalErrorException()
        }
    }

    override fun calculateType(): AbstractType {
        return when {
            isSuperType(INT_TYPE, leftType!!) && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            isSuperType(NUMBER_TYPE, leftType) && isSuperType(NUMBER_TYPE, rightType) -> DECIMAL_TYPE
            else -> throw WilesTypeException(leftType, rightType)
        }
    }
}


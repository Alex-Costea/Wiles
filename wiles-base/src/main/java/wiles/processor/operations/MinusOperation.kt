package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.MINUS_INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.NUMBER_TYPE
import wiles.processor.utils.InterpreterUtils.isSuperType
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInfinity
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesMinusInfinity
import wiles.shared.errors.WilesTypeException

class MinusOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun calculateObject() : Any?
    {
        return when{
            left == null && rightObj is WilesInteger -> -rightObj
            left == null && rightObj is WilesDecimal -> -rightObj
            !bothKnown -> null
            leftObj is WilesInteger && rightObj is WilesInteger -> leftObj - rightObj
            leftObj is WilesInteger && rightObj is WilesDecimal -> leftObj - rightObj
            leftObj is WilesDecimal && rightObj is WilesInteger -> leftObj - rightObj
            leftObj is WilesDecimal && rightObj is WilesDecimal -> leftObj - rightObj
            rightObj is WilesInfinity -> WilesMinusInfinity
            else -> null
        }
    }

    override fun calculateType(): AbstractType {
        return when {
            left == null && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            left == null && isSuperType(DECIMAL_TYPE, rightType) -> DECIMAL_TYPE
            isSuperType(INFINITY_TYPE, rightType) -> MINUS_INFINITY_TYPE
            isSuperType(INT_TYPE, leftType!!) && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            isSuperType(NUMBER_TYPE, leftType) && isSuperType(NUMBER_TYPE, rightType) -> DECIMAL_TYPE
            else -> throw WilesTypeException(leftType, rightType)
        }
    }
}
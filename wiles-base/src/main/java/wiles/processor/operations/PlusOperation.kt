package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.AbstractType.Companion.ANYTHING_TYPE
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.FINITE_NUMBER_TYPE
import wiles.processor.types.AbstractType.Companion.INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.MINUS_INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.NUMBER_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.types.WilesType
import wiles.processor.utils.InterpreterUtils.isSuperType
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInfinity
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesMinusInfinity
import wiles.shared.errors.WilesTypeException

class PlusOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {

    override fun calculateObject() : Any?
    {
        return when{
            !bothKnown -> null
            left == null && rightObj is WilesInteger -> +rightObj
            left == null && rightObj is WilesDecimal -> +rightObj
            leftObj is WilesInteger && rightObj is WilesInteger -> leftObj + rightObj
            leftObj is WilesInteger && rightObj is WilesDecimal -> leftObj + rightObj
            leftObj is WilesDecimal && rightObj is WilesInteger -> leftObj + rightObj
            leftObj is WilesDecimal && rightObj is WilesDecimal -> leftObj + rightObj
            leftObj == null && rightObj is WilesInfinity -> WilesInfinity
            leftObj == null && rightObj is WilesMinusInfinity -> WilesMinusInfinity
            leftObj == null || rightObj == null -> null
            leftObj is WilesInfinity && rightObj is WilesMinusInfinity -> throw ArithmeticException()
            leftObj is WilesMinusInfinity && rightObj is WilesInfinity -> throw ArithmeticException()
            leftObj is WilesInfinity -> WilesInfinity
            leftObj is WilesMinusInfinity -> WilesMinusInfinity
            rightObj is WilesInfinity -> WilesInfinity
            rightObj is WilesMinusInfinity -> WilesMinusInfinity
            else -> leftObj.toString() + rightObj.toString()
        }
    }

    override fun calculateType(): WilesType {
        return when {
            left == null && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            left == null && isSuperType(DECIMAL_TYPE, rightType) -> DECIMAL_TYPE
            left == null && isSuperType(INFINITY_TYPE, rightType) -> INFINITY_TYPE
            left == null -> throw WilesTypeException(leftType, rightType)
            isSuperType(MINUS_INFINITY_TYPE, rightType) -> MINUS_INFINITY_TYPE
            isSuperType(INFINITY_TYPE, leftType!!) -> INFINITY_TYPE
            isSuperType(MINUS_INFINITY_TYPE, leftType) -> MINUS_INFINITY_TYPE
            isSuperType(INT_TYPE, leftType) && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            isSuperType(FINITE_NUMBER_TYPE, leftType) && isSuperType(FINITE_NUMBER_TYPE, rightType) -> DECIMAL_TYPE
            isSuperType(NUMBER_TYPE, leftType) && isSuperType(NUMBER_TYPE, rightType) -> NUMBER_TYPE
            isSuperType(TEXT_TYPE, leftType) && isSuperType(ANYTHING_TYPE, rightType) -> TEXT_TYPE
            isSuperType(ANYTHING_TYPE, leftType) && isSuperType(TEXT_TYPE, rightType) -> TEXT_TYPE
            else -> throw WilesTypeException(leftType, rightType)
        }
    }
}
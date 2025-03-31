package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.FINITE_NUMBER_TYPE
import wiles.processor.types.AbstractType.Companion.INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.MINUS_INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.NUMBER_TYPE
import wiles.processor.utils.InterpreterUtils.equalsValue
import wiles.processor.utils.InterpreterUtils.isSuperType
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInfinity
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesMinusInfinity
import wiles.shared.errors.WilesTypeException

class TimesOperation(left: Value?, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {

    override fun calculateObject() : Any?
    {
        return when{
            !bothKnown -> null
            leftObj is WilesInteger && rightObj is WilesInteger -> leftObj * rightObj
            leftObj is WilesInteger && rightObj is WilesDecimal -> leftObj * rightObj
            leftObj is WilesDecimal && rightObj is WilesInteger -> leftObj * rightObj
            leftObj is WilesDecimal && rightObj is WilesDecimal -> leftObj * rightObj
            (leftObj is WilesInfinity || leftObj is WilesMinusInfinity)
                    && equalsValue(rightObj, "0") -> throw ArithmeticException()
            (rightObj is WilesInfinity || rightObj is WilesMinusInfinity)
                    && equalsValue(leftObj, "0") -> throw ArithmeticException()
            leftObj is WilesInfinity -> WilesInfinity
            leftObj is WilesMinusInfinity -> WilesMinusInfinity
            rightObj is WilesInfinity -> WilesInfinity
            rightObj is WilesMinusInfinity -> WilesMinusInfinity
            else -> null
        }
    }


    override fun calculateType(): AbstractType {
        return when {
            isSuperType(INT_TYPE, leftType!!) && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            isSuperType(INFINITY_TYPE, rightType) -> INFINITY_TYPE
            isSuperType(MINUS_INFINITY_TYPE, rightType) -> MINUS_INFINITY_TYPE
            isSuperType(INFINITY_TYPE, leftType) -> INFINITY_TYPE
            isSuperType(MINUS_INFINITY_TYPE, leftType) -> MINUS_INFINITY_TYPE
            isSuperType(FINITE_NUMBER_TYPE, leftType) && isSuperType(FINITE_NUMBER_TYPE, rightType) -> DECIMAL_TYPE
            isSuperType(NUMBER_TYPE, leftType) && isSuperType(NUMBER_TYPE, rightType) -> NUMBER_TYPE
            else -> throw WilesTypeException(leftType, rightType)
        }
    }
}


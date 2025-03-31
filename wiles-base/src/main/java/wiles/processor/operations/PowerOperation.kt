package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.FINITE_NUMBER_TYPE
import wiles.processor.types.AbstractType.Companion.INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.INVALID_TYPE
import wiles.processor.types.AbstractType.Companion.MINUS_INFINITY_TYPE
import wiles.processor.types.AbstractType.Companion.NUMBER_TYPE
import wiles.processor.utils.InterpreterUtils.equalsValue
import wiles.processor.utils.InterpreterUtils.isSuperType
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInfinity
import wiles.processor.values.WilesInteger
import wiles.processor.values.WilesMinusInfinity
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

            //infinity nonsense
            leftObj is WilesInfinity && rightObj is WilesMinusInfinity -> throw ArithmeticException()
            equalsValue(leftObj, "1") && (rightObj is WilesInfinity || rightObj is WilesMinusInfinity)
                -> throw ArithmeticException()
            leftObj is WilesMinusInfinity -> throw ArithmeticException()
            equalsValue(leftObj, "0") && rightObj is WilesMinusInfinity -> WilesInfinity
            rightObj is WilesMinusInfinity -> WilesInteger(0)
            leftObj is WilesInfinity && isNegative(rightObj) -> WilesInteger(0)
            leftObj is WilesInfinity -> WilesInfinity
            rightObj is WilesInfinity && isBetweenZeroAndOne(leftObj) -> WilesInteger(0)
            rightObj is WilesInfinity -> WilesInfinity

            else -> null
        }
    }

    private fun isBetweenZeroAndOne(obj: Any?): Boolean {
        if(obj is WilesInteger)
            return obj > WilesInteger("0") && obj < WilesInteger("1")
        if(obj is WilesDecimal)
            return obj > WilesDecimal("0") && obj < WilesDecimal("1")
        return false
    }

    private fun isNegative(obj: Any?): Boolean {
        if(obj is WilesInteger)
            return obj < WilesInteger("0")
        if(obj is WilesDecimal)
            return obj < WilesDecimal("0")
        return false
    }

    override fun calculateType(): AbstractType {
        return when {
            isSuperType(INT_TYPE, leftType!!) && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            isSuperType(INFINITY_TYPE, leftType) -> NUMBER_TYPE
            isSuperType(INFINITY_TYPE, rightType) -> NUMBER_TYPE
            isSuperType(MINUS_INFINITY_TYPE, leftType) -> INVALID_TYPE //nonsense but it'll throw
            isSuperType(MINUS_INFINITY_TYPE, rightType) -> INT_TYPE.exactly(0)
            isSuperType(FINITE_NUMBER_TYPE, leftType) && isSuperType(FINITE_NUMBER_TYPE, rightType) -> DECIMAL_TYPE
            isSuperType(NUMBER_TYPE, leftType) && isSuperType(NUMBER_TYPE, rightType) -> NUMBER_TYPE
            else -> throw WilesTypeException(leftType, rightType)
        }
    }
}


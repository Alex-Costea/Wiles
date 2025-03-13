package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.ANYTHING_TYPE
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INT_TYPE
import wiles.processor.types.AbstractType.Companion.NUMBER_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.utils.InterpreterUtils.isSuperType
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger
import wiles.shared.errors.InternalErrorException
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
            leftObj == null || rightObj == null -> InternalErrorException()
            else -> leftObj.toString() + rightObj.toString()
        }
    }

    override fun calculateType(): AbstractType {
        return when {
            left == null && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            left == null && isSuperType(DECIMAL_TYPE, rightType) -> DECIMAL_TYPE
            left == null -> throw WilesTypeException(leftType, rightType)
            isSuperType(INT_TYPE, leftType!!) && isSuperType(INT_TYPE, rightType) -> INT_TYPE
            isSuperType(NUMBER_TYPE, leftType) && isSuperType(NUMBER_TYPE, rightType) -> DECIMAL_TYPE
            isSuperType(TEXT_TYPE, leftType) && isSuperType(ANYTHING_TYPE, rightType) -> TEXT_TYPE
            isSuperType(ANYTHING_TYPE, leftType) && isSuperType(TEXT_TYPE, rightType) -> TEXT_TYPE
            else -> throw WilesTypeException(leftType, rightType)
        }
    }
}
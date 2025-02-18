package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.enums.WilesTypes
import wiles.processor.types.IntegerType
import wiles.processor.values.Value
import java.math.BigInteger

class PlusOperation(left: Value, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {
        if(leftType.typeName == WilesTypes.INT && rightType.typeName == WilesTypes.INT)
        {
            if(leftType.isSingleton() && rightType.isSingleton())
            {
                val leftInt = leftType.getValue() as BigInteger
                val rightInt = rightType.getValue() as BigInteger
                val newInt : BigInteger = leftInt + rightInt
                val newType = IntegerType().singletonValueOf(newInt)
                return Value(newInt, newType, null)
            }
            else return Value(null, IntegerType(), null)
        }
        else TODO("Non-int addition operation")
    }
}
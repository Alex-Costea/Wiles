package wiles.interpreter.operations

import wiles.interpreter.data.InterpreterContext
import wiles.interpreter.enums.WilesTypes
import wiles.interpreter.types.IntegerType
import wiles.interpreter.values.Value
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
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
            if(leftObj is BigInteger && rightObj is BigInteger)
            {
                val newInt : BigInteger = leftObj + rightObj
                val newType = IntegerType().singletonValueOf(newInt)
                return Value(newInt, newType, false, isKnown = true)
            }
            else return Value(null, IntegerType(), false, isKnown = false)
        }
        else TODO("Non-int addition operation")
    }
}
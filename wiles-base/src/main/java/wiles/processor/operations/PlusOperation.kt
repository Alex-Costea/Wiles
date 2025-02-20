package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.enums.KnownStatus
import wiles.processor.enums.VariableStatus
import wiles.processor.enums.WilesTypes
import wiles.processor.types.IntegerType
import wiles.processor.types.StringType
import wiles.processor.values.Value
import java.math.BigInteger

class PlusOperation(left: Value, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {
    override fun getNewValue(): Value {

        // addition
        if(leftType.typeName == WilesTypes.INT && rightType.typeName == WilesTypes.INT)
        {
            if(left.isKnown() && right.isKnown())
            {
                val newInt : BigInteger = leftObj as BigInteger+ rightObj as BigInteger
                val newType = IntegerType().singletonValueOf(newInt)
                return Value(newInt, newType, VariableStatus.Val, KnownStatus.Known)
            }
            else return Value(null, IntegerType(), VariableStatus.Val, KnownStatus.Known)
        }

        //concat
        if(leftType.typeName == WilesTypes.STRING || rightType.typeName == WilesTypes.STRING)
        {
            if(left.isKnown() && right.isKnown())
            {
                val newString : String = leftObj.toString() + rightObj.toString()
                val newType = StringType().singletonValueOf(newString)
                return Value(newString, newType, VariableStatus.Val, KnownStatus.Known)
            }
            else return Value(null, StringType(), VariableStatus.Val, KnownStatus.Known)
        }
        else TODO("Non-int addition operation")
    }
}
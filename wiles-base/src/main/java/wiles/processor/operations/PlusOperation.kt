package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.enums.KnownStatus
import wiles.processor.enums.VariableStatus
import wiles.processor.enums.WilesTypes
import wiles.processor.types.AbstractType
import wiles.processor.types.IntegerType
import wiles.processor.types.StringType
import wiles.processor.values.Value
import java.math.BigInteger

class PlusOperation(left: Value, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {

    override fun calculateObject() : Any?
    {
        if(!bothKnown)
            return null
        if(leftObj is BigInteger && rightObj is BigInteger) return leftObj + rightObj
        return leftObj.toString() + rightObj.toString()
    }

    override fun calculateType(): AbstractType {
        //TODO: handle sum types
        val newType = when {
            leftType.typeName == WilesTypes.INT && rightType.typeName == WilesTypes.INT -> IntegerType()
            leftType.typeName == WilesTypes.STRING || rightType.typeName == WilesTypes.STRING -> StringType()
            else -> TODO("Can't add these types")
        }
        return newType
    }

    override fun getNewValue(): Value {
        val newObject = calculateObject()
        val newType = calculateType()
        if(bothKnown)
            newType.singletonValueOf(newObject!!)
        return Value(newObject, newType, VariableStatus.Val, if(bothKnown) KnownStatus.Known else KnownStatus.Unknown)
    }
}
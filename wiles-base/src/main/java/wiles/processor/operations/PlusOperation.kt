package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValueProps.Companion.KNOWN_EXPR
import wiles.processor.data.ValueProps.Companion.UNKNOWN_EXPR
import wiles.processor.types.AbstractType
import wiles.processor.types.DecimalType
import wiles.processor.types.IntegerType
import wiles.processor.types.TextType
import wiles.processor.utils.TypeUtils.DECIMAL_TYPE
import wiles.processor.utils.TypeUtils.INTEGER_TYPE
import wiles.processor.utils.TypeUtils.STRING_TYPE
import wiles.processor.utils.TypeUtils.isSuperType
import wiles.processor.values.Value
import wiles.processor.values.WilesDecimal
import wiles.processor.values.WilesInteger

class PlusOperation(left: Value, right: Value, context: InterpreterContext) : AbstractOperation(left, right, context) {

    override fun calculateObject() : Any?
    {
        if(!bothKnown)
            return null
        return when{
            leftObj is WilesInteger && rightObj is WilesInteger -> leftObj + rightObj
            leftObj is WilesInteger && rightObj is WilesDecimal -> leftObj + rightObj
            leftObj is WilesDecimal && rightObj is WilesInteger -> leftObj + rightObj
            leftObj is WilesDecimal && rightObj is WilesDecimal -> leftObj + rightObj
            else -> leftObj.toString() + rightObj.toString()
        }
    }

    override fun calculateType(): AbstractType {
        //TODO: handle sum types
        val leftIsInt = isSuperType(INTEGER_TYPE, leftType!!)
        val rightIsInt = isSuperType(INTEGER_TYPE, rightType)
        val leftIsDecimal = isSuperType(DECIMAL_TYPE, leftType)
        val rightIsDecimal = isSuperType(DECIMAL_TYPE, rightType)
        val newType = when {
            leftIsInt && rightIsInt -> IntegerType()
            leftIsInt && rightIsDecimal -> DecimalType()
            leftIsDecimal && rightIsInt -> DecimalType()
            leftIsDecimal && rightIsDecimal -> DecimalType()
            isSuperType(STRING_TYPE, leftType) || isSuperType(STRING_TYPE, rightType) -> TextType()
            else -> TODO("Can't add these types")
        }
        return newType
    }

    override fun getNewValue(): Value {
        val newObject = calculateObject()
        val newType = calculateType()
        if(bothKnown)
            newType.singletonValueOf(newObject!!)
        return Value(newObject, newType, if(bothKnown) KNOWN_EXPR else UNKNOWN_EXPR)
    }
}
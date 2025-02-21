package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.enums.KnownStatus
import wiles.processor.enums.VariableStatus
import wiles.processor.types.*
import wiles.processor.values.Value

class InternalOperation(right: Value, context: InterpreterContext) : AbstractOperation(null, right, context)
{
    override fun getNewValue(): Value {
        return Value(calculateObject(), calculateType(), VariableStatus.Val, KnownStatus.Known)
    }

    override fun calculateObject(): Any? {
        return GET_VALUES[rightObj as String]
    }

    override fun calculateType(): AbstractType {
        return GET_TYPES[rightObj as String]!!
    }

    companion object{
        private const val TRUE_ID = "true"
        private const val FALSE_ID = "false"
        private const val NOTHING_ID = "nothing"
        private const val INT_ID = "int"
        private const val TEXT_ID = "text"
        private const val DECIMAL_ID = "decimal"

        val GET_VALUES = mapOf(
            TRUE_ID to true,
            FALSE_ID to false,
            NOTHING_ID to null,
            INT_ID to IntegerType(),
            TEXT_ID to StringType(),
            DECIMAL_ID to DecimalType(),
        )
        val GET_TYPES = mapOf(
            TRUE_ID to BooleanType(),
            FALSE_ID to BooleanType(),
            NOTHING_ID to NothingType(),
            INT_ID to TypeType(),
            TEXT_ID to TypeType(),
            DECIMAL_ID to TypeType(),
        )
    }
}
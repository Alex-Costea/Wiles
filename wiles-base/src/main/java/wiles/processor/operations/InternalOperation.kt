package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.enums.KnownStatus
import wiles.processor.enums.VariableStatus
import wiles.processor.types.*
import wiles.processor.values.Value

class InternalOperation(right: Value, context: InterpreterContext) : AbstractOperation(null, right, context)
{
    private val name = (rightObj as String).uppercase()
    override fun getNewValue(): Value {
        return Value(calculateObject(), calculateType(), VariableStatus.Val, KnownStatus.Known)
    }

    override fun calculateObject(): Any? {
        return GET_VALUES[name]
    }

    override fun calculateType(): AbstractType {
        return GET_TYPES[name]!!
    }

    companion object{
        private const val TRUE_ID = "TRUE"
        private const val FALSE_ID = "FALSE"
        private const val NOTHING_ID = "NOTHING"
        private const val INT_ID = "INT"
        private const val TEXT_ID = "TEXT"
        private const val DECIMAL_ID = "DECIMAL"

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
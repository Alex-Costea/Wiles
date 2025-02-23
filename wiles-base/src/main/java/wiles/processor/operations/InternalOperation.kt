package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValueProps.Companion.DEFAULT_EXPR
import wiles.processor.types.*
import wiles.processor.data.Value
import wiles.processor.values.WilesNothing

class InternalOperation(right: Value, context: InterpreterContext) : AbstractOperation(null, right, context)
{
    private val name = (rightObj as String).uppercase()
    override fun getNewValue(): Value {
        return Value(calculateObject(), calculateType(), DEFAULT_EXPR)
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
        private const val ANYTHING_ID = "ANYTHING"

        val GET_VALUES = mapOf(
            TRUE_ID to true,
            FALSE_ID to false,
            NOTHING_ID to WilesNothing,
            INT_ID to IntegerType(),
            TEXT_ID to TextType(),
            DECIMAL_ID to DecimalType(),
            ANYTHING_ID to AnythingType(),
        )
        val GET_TYPES = mapOf(
            TRUE_ID to BooleanType(),
            FALSE_ID to BooleanType(),
            NOTHING_ID to NothingType(),
            INT_ID to TypeType(),
            TEXT_ID to TypeType(),
            DECIMAL_ID to TypeType(),
            ANYTHING_ID to TypeType(),
        )
    }
}
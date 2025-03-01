package wiles.processor.operations

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValueProps.Companion.DEFAULT_EXPR
import wiles.processor.functions.RandFunction
import wiles.processor.types.AbstractType
import wiles.processor.types.AbstractType.Companion.ANYTHING_TYPE
import wiles.processor.types.AbstractType.Companion.BOOLEAN_TYPE
import wiles.processor.types.AbstractType.Companion.DECIMAL_TYPE
import wiles.processor.types.AbstractType.Companion.INTEGER_TYPE
import wiles.processor.types.AbstractType.Companion.NOTHING_TYPE
import wiles.processor.types.AbstractType.Companion.TEXT_TYPE
import wiles.processor.types.AbstractType.Companion.TYPE_TYPE
import wiles.processor.types.FunctionType
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
        private const val RAND_ID = "RAND"

        private val RAND_FUNCTION = RandFunction()

        val GET_VALUES = mapOf(
            TRUE_ID to true,
            FALSE_ID to false,
            NOTHING_ID to WilesNothing,
            INT_ID to INTEGER_TYPE,
            TEXT_ID to TEXT_TYPE,
            DECIMAL_ID to DECIMAL_TYPE,
            ANYTHING_ID to ANYTHING_TYPE,
            RAND_ID to RandFunction(),
        )
        val GET_TYPES = mapOf(
            TRUE_ID to BOOLEAN_TYPE,
            FALSE_ID to BOOLEAN_TYPE,
            NOTHING_ID to NOTHING_TYPE,
            INT_ID to TYPE_TYPE,
            TEXT_ID to TYPE_TYPE,
            DECIMAL_ID to TYPE_TYPE,
            ANYTHING_ID to TYPE_TYPE,
            RAND_ID to FunctionType(RAND_FUNCTION, DECIMAL_TYPE)
        )
    }
}
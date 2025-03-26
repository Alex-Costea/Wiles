package wiles.processor.functions

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap
import wiles.processor.types.AbstractType
import wiles.processor.values.WilesDecimal
import kotlin.random.Random

class RandFunction : WilesFunction() {
    override val pure = false
    override fun invoke(values: ValuesMap, context : InterpreterContext): Value {
        if(context.isCompiling)
            return Value(null, AbstractType.DECIMAL_TYPE)
        val sb = StringBuilder("0.")
        for(i in 0..15)
            sb.append(Random.nextInt(0,10).digitToChar())
        val obj = WilesDecimal(sb.toString())
        return Value(obj, AbstractType.DECIMAL_TYPE.exactly(obj))
    }
}
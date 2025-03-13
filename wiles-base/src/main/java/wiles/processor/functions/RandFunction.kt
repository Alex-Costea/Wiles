package wiles.processor.functions

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValuesMap
import wiles.processor.values.WilesDecimal
import kotlin.random.Random

class RandFunction : WilesFunction() {
    override fun invoke(values: ValuesMap, context : InterpreterContext): WilesDecimal {
        val sb = StringBuilder("0.")
        for(i in 0..15)
            sb.append(Random.nextInt(0,10).digitToChar())
        return WilesDecimal(sb.toString())
    }
}
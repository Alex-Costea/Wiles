package wiles.processor.functions

import wiles.processor.data.ValuesMap
import wiles.processor.values.WilesDecimal
import kotlin.random.Random

class RandFunction : WilesFunction() {
    override fun invoke(values : ValuesMap): Any {
        return WilesDecimal(Random.nextDouble().toString())
    }
}
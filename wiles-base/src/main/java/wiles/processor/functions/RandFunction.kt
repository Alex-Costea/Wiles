package wiles.processor.functions

import wiles.processor.values.WilesDecimal
import kotlin.random.Random

class RandFunction : WilesFunction() {
    override fun invoke(): Any {
        return WilesDecimal(Random.nextDouble().toString())
    }
}
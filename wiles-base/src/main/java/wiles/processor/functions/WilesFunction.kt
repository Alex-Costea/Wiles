package wiles.processor.functions

import wiles.processor.data.InterpreterContext
import wiles.processor.data.Value
import wiles.processor.data.ValuesMap

abstract class WilesFunction : (ValuesMap, InterpreterContext) -> Value
{
    abstract val pure : Boolean
    override fun toString(): String {
        return "WilesFunction"
    }
}
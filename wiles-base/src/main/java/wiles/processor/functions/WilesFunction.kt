package wiles.processor.functions

import wiles.processor.data.InterpreterContext
import wiles.processor.data.ValuesMap

abstract class WilesFunction : (ValuesMap, InterpreterContext) -> Any?
{
    override fun toString(): String {
        return "WilesFunction"
    }
}
package wiles.processor.functions

import wiles.processor.data.ValuesMap

abstract class WilesFunction : (ValuesMap) -> Any
{
    override fun toString(): String {
        return "WilesFunction"
    }
}
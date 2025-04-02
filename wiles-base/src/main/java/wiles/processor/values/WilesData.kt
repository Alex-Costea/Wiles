package wiles.processor.values

import wiles.processor.data.ValuesMap
import wiles.processor.utils.InterpreterUtils.objToStringInternally

data class WilesData(val values : ValuesMap){
    override fun toString(): String {
        return "<<" + values.map {
            val valueText = objToStringInternally(it.value.value.getObj())
            "\"${it.key}\" := $valueText" }
            . joinToString(", ") + ">>"
    }
}
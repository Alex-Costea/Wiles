package wiles.processor.values

import wiles.processor.data.ValuesMap

data class WilesData(val values : ValuesMap){
    override fun toString(): String {
        return "<<" + values.map {
            val value = it.value.value.getObj()
            val valueText = if(value is String) "\"$value\"" else value.toString()
            "\"${it.key}\" := $valueText" }
            . joinToString(", ") + ">>"
    }
}
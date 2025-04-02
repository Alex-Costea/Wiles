package wiles.processor.types

import wiles.processor.data.ValuesMap
import wiles.processor.utils.InterpreterUtils.objToStringInternally
import wiles.processor.values.WilesUndefined

class FunctionType(exactValue: Any? = null,
                   val params : ValuesMap,
                   val yieldsType : WilesType) : AbstractType(exactValue) {

    override fun toString(): String {
        return "fun(${params.map { 
            val paramName = it.key.substring(1)
            val paramValue = if(it.value.value.isLazy()) WilesUndefined else it.value.value.getObj()
            val part2 = if(paramValue == null || paramValue == WilesUndefined) " : ${it.value.value.getType()}"
                else " = ${objToStringInternally(paramValue)}"
            paramName + part2
        }.joinToString(", ")}) -> $yieldsType"
    }

    override fun ofValue(obj : Any?): AbstractType {
        return FunctionType(obj, params, yieldsType)
    }
}
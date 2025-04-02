package wiles.processor.types

import wiles.processor.data.ValuesMap
import wiles.processor.enums.VariableStatus
import wiles.processor.utils.InterpreterUtils.objToStringInternally
import wiles.processor.values.WilesUndefined

class FunctionType(val params : ValuesMap,
                   val yieldsType : WilesType) : AbstractType(null) {

    override fun toString(): String {
        return "fun(${params.map { 
            val part1 = it.key.substring(1)
            val paramValue = if(it.value.value.isLazy()) WilesUndefined else it.value.value.getObj()
            val part2 = if(paramValue == null || paramValue == WilesUndefined) " : ${it.value.value.getType()}"
                else " = ${objToStringInternally(paramValue)}"
            val part0 = when(it.value.variableStatus)
            {
                VariableStatus.Comptime -> "const "
                VariableStatus.ComptimeArg -> "const arg "
                VariableStatus.Arg -> "arg "
                else -> ""
            }
            part0 + part1 + part2
        }.joinToString(", ")}) -> $yieldsType"
    }

    override fun ofValue(obj : Any?): AbstractType {
        return FunctionType(params, yieldsType)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as FunctionType

        if (params != other.params) return false
        if (yieldsType != other.yieldsType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + params.hashCode()
        result = 31 * result + yieldsType.hashCode()
        return result
    }

}
package wiles.processor.data

import wiles.processor.enums.VariableStatus
import wiles.processor.types.WilesType

data class ValueData(
    val value: Value,
    val variableStatus: VariableStatus,
    val comptimeType : WilesType = value.getType(),
) {
    fun isVariable(): Boolean {
        return variableStatus == VariableStatus.Var
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueData

        if (value != other.value) return false
        if (variableStatus != other.variableStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + variableStatus.hashCode()
        return result
    }


}
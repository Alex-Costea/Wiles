package wiles.processor.data

import wiles.processor.enums.VariableStatus
import wiles.processor.types.WilesType

data class ValueData(
    val value: Value,
    val variableStatus: VariableStatus,
    private val comptimeType : WilesType? = null
) {
    fun isVariable(): Boolean {
        return variableStatus == VariableStatus.Var
    }

    fun getComptimeType() : WilesType
    {
        if(comptimeType != null)
            return comptimeType
        return value.getType()
    }
}
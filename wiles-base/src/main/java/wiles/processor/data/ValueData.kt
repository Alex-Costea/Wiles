package wiles.processor.data

import wiles.processor.enums.VariableStatus
import wiles.processor.types.AbstractType

data class ValueData(
    val value: Value,
    val variableStatus: VariableStatus,
    private val comptimeType : AbstractType? = null
) {
    fun isVariable(): Boolean {
        return variableStatus == VariableStatus.Var
    }

    fun getComptimeType() : AbstractType
    {
        if(comptimeType != null)
            return comptimeType
        return value.getType()
    }
}
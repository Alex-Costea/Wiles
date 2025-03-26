package wiles.processor.data

import wiles.processor.enums.VariableStatus

data class ValueData(
    val value: Value,
    val variableStatus: VariableStatus
) {
    fun isVariable(): Boolean {
        return variableStatus == VariableStatus.Var
    }
}
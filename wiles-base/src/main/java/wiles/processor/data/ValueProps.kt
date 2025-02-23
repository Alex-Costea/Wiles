package wiles.processor.data

import wiles.processor.enums.VariableStatus

class ValueProps(private val variableStatus: VariableStatus = VariableStatus.Const) {

    fun variableStatus() : VariableStatus{
        return variableStatus
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueProps

        return variableStatus == other.variableStatus
    }

    override fun hashCode(): Int {
        val result = variableStatus.hashCode()
        return result
    }

    override fun toString(): String {
        return "[variableStatus=$variableStatus]"
    }


    companion object{
        val DEFAULT_EXPR = ValueProps()
        val VARIABLE_EXPR = ValueProps(VariableStatus.Var)
    }
}
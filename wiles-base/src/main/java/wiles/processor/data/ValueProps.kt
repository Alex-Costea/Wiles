package wiles.processor.data

import wiles.processor.enums.KnownStatus
import wiles.processor.enums.VariableStatus

class ValueProps(private val knownStatus : KnownStatus,
                 private val variableStatus: VariableStatus = VariableStatus.Val
) {

    fun variableStatus() : VariableStatus{
        return variableStatus
    }

    fun knownStatus() : KnownStatus{
        return knownStatus
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueProps

        if (variableStatus != other.variableStatus) return false
        if (knownStatus != other.knownStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variableStatus.hashCode()
        result = 31 * result + knownStatus.hashCode()
        return result
    }

    override fun toString(): String {
        return "ValueProps(variableStatus=$variableStatus, knownStatus=$knownStatus)"
    }


    companion object{
        val KNOWN_EXPR = ValueProps(KnownStatus.Known)
        val UNKNOWN_EXPR = ValueProps(KnownStatus.Unknown)
    }
}
package wiles.processor.values

import wiles.processor.enums.KnownStatus
import wiles.processor.enums.VariableStatus
import wiles.processor.types.AbstractType

class Value(
    private val obj: Any?,
    private val type: AbstractType,
    private val variableStatus: VariableStatus,
    private val knownStatus : KnownStatus
) {
    fun getObj() : Any?{
        return obj
    }

    fun getType() : AbstractType{
        return type
    }

    fun variableStatus() : VariableStatus{
        return variableStatus
    }

    fun knownStatus() : KnownStatus {
        return knownStatus
    }

    fun isVariable() : Boolean {
        return variableStatus == VariableStatus.Var
    }

    fun isKnown() : Boolean{
        return knownStatus == KnownStatus.Known
    }

    override fun toString(): String {
        return "Value(obj=$obj, type=$type, isVariable=$variableStatus, isKnown=$knownStatus)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (obj != other.obj) return false
        if (type != other.type) return false
        if (variableStatus != other.variableStatus()) return false
        if (knownStatus != other.knownStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + variableStatus.hashCode()
        result = 31 * result + knownStatus.hashCode()
        return result
    }


}
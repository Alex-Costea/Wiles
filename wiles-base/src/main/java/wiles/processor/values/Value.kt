package wiles.processor.values

import wiles.processor.data.ValueProps
import wiles.processor.enums.VariableStatus
import wiles.processor.types.AbstractType

class Value(
    private val obj: Any?,
    private val type: AbstractType,
    private val props : ValueProps
) {
    fun getObj() : Any?{
        return obj
    }

    fun getType() : AbstractType{
        return type
    }

    override fun toString(): String {
        return "Value(obj=$obj, type=$type, props=$props)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (obj != other.obj) return false
        if (type != other.type) return false
        if (props != other.props) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + props.hashCode()
        return result
    }

    fun isVariable() : Boolean {
        return getVariableStatus() == VariableStatus.Var
    }

    fun isKnown() : Boolean {
        return obj != null
    }

    private fun getVariableStatus() : VariableStatus {
        return props.variableStatus()
    }

}
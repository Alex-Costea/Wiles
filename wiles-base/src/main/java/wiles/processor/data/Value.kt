package wiles.processor.data

import wiles.processor.enums.VariableStatus
import wiles.processor.types.AbstractType
import wiles.processor.values.WilesLazyObject

class Value(
    private val obj: Any?,
    private val type: AbstractType,
    private val props : ValueProps
) {
    fun getObj() : Any?{
        if(obj is WilesLazyObject)
            return obj.getObject()
        return obj
    }

    fun getType() : AbstractType{
        return type
    }

    private fun getObjString() : String
    {
        if(obj is WilesLazyObject)
        {
            if(!obj.hasBeenComputed())
                return "LazyObject"
        }
        return obj.toString()
    }

    override fun toString(): String {
        return "Value(obj=${getObjString()}, type=$type, props=$props)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (getObj() != other.getObj()) return false
        if (type != other.type) return false
        if (props != other.props) return false

        return true
    }

    override fun hashCode(): Int {
        var result = getObj()?.hashCode() ?: 0
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
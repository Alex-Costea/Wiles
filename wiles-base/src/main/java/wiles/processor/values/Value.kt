package wiles.processor.values

import wiles.processor.types.AbstractType

class Value(
    private val obj: Any?,
    private val type: AbstractType,
    private val isVariable: Boolean,
    private val isKnown : Boolean
) {
    fun getObj() : Any?{
        return obj
    }

    fun getType() : AbstractType{
        return type
    }

    fun isVariable() : Boolean{
        return isVariable
    }

    fun isKnown() : Boolean{
        return isKnown
    }

    override fun toString(): String {
        return "Value(obj=$obj, type=$type, isVariable=$isVariable, isKnown=$isKnown)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (obj != other.obj) return false
        if (type != other.type) return false
        if (isVariable != other.isVariable()) return false
        if (isKnown != other.isKnown) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + isVariable.hashCode()
        result = 31 * result + isKnown.hashCode()
        return result
    }


}
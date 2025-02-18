package wiles.processor.values

import wiles.processor.types.AbstractType

class Value(
    private val obj: Any?,
    private val type: AbstractType,
    private val isVariable: Boolean
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

    override fun toString(): String {
        val isVar = if(isVariable()) ", isVariable" else ""
        return "Value(obj=$obj, type=$type$isVar)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (obj != other.obj) return false
        if (type != other.type) return false
        if (isVariable != other.isVariable()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + isVariable.hashCode()
        return result
    }


}
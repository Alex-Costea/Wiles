package wiles.interpreter.values

import wiles.interpreter.types.AbstractType

class Value(
    private val obj: Any?,
    private val type: AbstractType,
    private val varName : String?
) {
    fun getObj() : Any?{
        return obj
    }

    fun getType() : AbstractType{
        return type
    }

    fun isVariable() : Boolean{
        return varName != null
    }

    override fun toString(): String {
        val varNameSection = if(isVariable()) ", varName=$varName" else ""
        return "Value(obj=$obj, type=$type$varNameSection)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (obj != other.obj) return false
        if (type != other.type) return false
        if (varName != other.varName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = obj?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + (varName?.hashCode() ?: 0)
        return result
    }


}
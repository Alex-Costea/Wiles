package wiles.interpreter.values

import wiles.interpreter.types.AbstractType

class Value(
    private var obj: Any?,
    private val type: AbstractType
) {
    fun getObj() : Any?{
        return obj
    }

    fun getType() : AbstractType{
        return type
    }

    override fun toString(): String {
        return "Value(obj=$obj, type=$type)"
    }

}
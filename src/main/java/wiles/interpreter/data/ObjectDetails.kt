package wiles.interpreter.data

import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import java.util.function.Function

data class ObjectDetails(var value : Any?, var type : JSONStatement)
{

    private fun cloneValue(value : Any?) : Any?
    {
        val newValue = when(value)
        {
            is Double, is Long, is String, is Boolean, null -> value
            is MutableList<*> -> value.map{cloneValue(it)}.toMutableList()
            is Function<*, *> -> value
            is ObjectDetails -> ObjectDetails(cloneValue(value.value),type.copyRemovingLocation())
            else -> throw InternalErrorException()
        }
        return newValue
    }

    fun makeMutable() : ObjectDetails
    {
        return cloneValue(this) as ObjectDetails
    }

    override fun toString(): String {
        return when(value) {
            null -> "nothing"
            is Function<*, *> -> type.components[0].toString()
            is MutableList<*> -> (value as MutableList<*>).joinToString(prefix = "[", postfix = "]")
            else -> value.toString()
        }
    }


}
package wiles.interpreter.data

import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.constants.TypeConstants.makeMutable
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
            else -> throw InternalErrorException()
        }
        return newValue
    }

    fun makeMutable() : ObjectDetails
    {
        return ObjectDetails(cloneValue(value), makeMutable(type.copyRemovingLocation()))
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
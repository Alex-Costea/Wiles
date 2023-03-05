package wiles.interpreter.data

import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.constants.TypeConstants.makeMutable

data class ObjectDetails(val value : Any?, val type : JSONStatement)
{

    private fun cloneValue(value : Any?) : Any?
    {
        val newValue = when(value)
        {
            is Double, is Long, is String, is Boolean, null -> value
            is MutableList<*> -> value.map{cloneValue(it)}.toMutableList()
            is java.util.function.Function<*, *> -> value
            else -> throw InternalErrorException()
        }
        return newValue
    }

    fun makeMutable() : ObjectDetails
    {
        return ObjectDetails(cloneValue(value), makeMutable(type.copyRemovingLocation()))
    }
}
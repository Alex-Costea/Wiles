package wiles.interpreter.data

import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement

data class ObjectDetails(val value : Any?, val type : JSONStatement)
{

    private fun cloneValue(value : Any?) : Any?
    {
        val newValue = when(value)
        {
            is Double, is Long, is String, is Boolean, null -> value
            is MutableList<*> -> value.map{cloneValue(it)}.toMutableList()
            is JSONStatement -> value.copyRemovingLocation()
            else -> throw InternalErrorException()
        }
        return newValue
    }

    fun clone() : ObjectDetails
    {
        return ObjectDetails(cloneValue(value), type.copyRemovingLocation())
    }
}
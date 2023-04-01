package wiles.interpreter.data

import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.constants.TypeConstants.MUTABLE_NULLABLE_ANYTHING
import wiles.shared.constants.TypeUtils
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import java.util.function.Function

class ObjectDetails(var value : Any?, var type : JSONStatement)
{

    private fun cloneValue(value: Any?, deep: Boolean) : Any?
    {
        val newValue = when(value)
        {
            is Double, is Long, is String, is Boolean, null -> value
            is MutableList<*> -> value.map{if(deep) cloneValue(it, true) else it}.toMutableList()
            is Function<*, *> -> value
            is ObjectDetails -> ObjectDetails(cloneValue(value.value, deep), value.type.copyRemovingLocation())
            else -> throw InternalErrorException()
        }
        return newValue
    }

    fun clone(deep : Boolean = true) : ObjectDetails
    {
        return cloneValue(this, deep) as ObjectDetails
    }

    private fun makeTypeMutable(obj : ObjectDetails) : ObjectDetails
    {
        var newObject = obj

        if(isFormerSuperTypeOfLatter(MUTABLE_NULLABLE_ANYTHING,obj.type))
            Unit
        else {
            newObject = obj.clone(deep = false)
            newObject.type = TypeUtils.makeMutable(obj.type)
        }
        return newObject
    }

    fun makeMutable() : ObjectDetails
    {
        return makeTypeMutable(this)
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
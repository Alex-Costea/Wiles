package wiles.interpreter.data

import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.constants.TypeConstants.LIST_OF_NULLABLE_ANYTHING_TYPE
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

    @Suppress("UNCHECKED_CAST")
    private fun makeTypeMutable(newObject : ObjectDetails)
    {
        if(isFormerSuperTypeOfLatter(MUTABLE_NULLABLE_ANYTHING,newObject.type))
            return
        val isList = isFormerSuperTypeOfLatter(LIST_OF_NULLABLE_ANYTHING_TYPE,newObject.type)
        if(isList)
            newObject.type.components[0] = TypeUtils.makeMutable(newObject.type.components[0])
        newObject.type = TypeUtils.makeMutable(newObject.type)
        if(isList)
        {
            for(component in newObject.value as MutableList<ObjectDetails>)
            {
                makeTypeMutable(component)
            }
        }
    }

    fun makeMutable() : ObjectDetails
    {
        val newObject = this.clone(deep = false)
        makeTypeMutable(newObject)
        return newObject
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
package wiles.interpreter.data

import wiles.checker.statics.InferrerUtils.addType
import wiles.shared.InternalErrorException
import wiles.shared.JSONStatement
import wiles.shared.constants.Tokens.MUTABLE_ID
import wiles.shared.constants.TypeConstants.MUTABLE_NULLABLE_ANYTHING
import wiles.shared.constants.TypeUtils
import wiles.shared.constants.TypeUtils.isFormerSuperTypeOfLatter
import wiles.shared.constants.Types.LIST_ID
import java.util.function.Function

class ObjectDetails(var value : Any?, type : JSONStatement)
{

    private lateinit var typeStatement : JSONStatement
    init {
        setType(type)
    }

    @Suppress("UNCHECKED_CAST")
    fun getType() : JSONStatement
    {
        val result = typeStatement.copy()
        if(typeStatement.name == MUTABLE_ID && typeStatement.components.getOrNull(0)?.name == LIST_ID)
        {
            val list = value as MutableList<ObjectDetails>
            val originalType = result.components[0].components[0]
            var newType : JSONStatement? = null
            for(component in list)
            {
                newType = if(newType == null)
                    component.getType()
                else addType(newType, component.getType())
            }
            result.components[0].components.clear()
            result.components[0].components.add(newType ?: originalType)
        }
        return result
    }

    fun setType(type : JSONStatement)
    {
        typeStatement = type.copy()
    }

    private fun cloneValue(value: Any?, deep: Boolean) : Any?
    {
        val newValue = when(value)
        {
            is Double, is Long, is String, is Boolean, null -> value
            is MutableList<*> -> value.map{if(deep) cloneValue(it, true) else it}.toMutableList()
            is LinkedHashMap<*,*> -> {
                val newValue = value.toList().map { if(!deep) it else
                    Pair(cloneValue(it.first, true), cloneValue(it.second,true))}
                    .associate { it.first to it.second }
                return newValue
            }
            is Function<*, *> -> value
            is ObjectDetails -> ObjectDetails(cloneValue(value.value, deep), value.getType().copyRemovingLocation())
            else -> throw InternalErrorException()
        }
        return newValue
    }

    fun clone(deep : Boolean = true) : ObjectDetails
    {
        return cloneValue(this, deep) as ObjectDetails
    }

    fun makeMutable() : ObjectDetails
    {
        var newObject = this

        if(isFormerSuperTypeOfLatter(MUTABLE_NULLABLE_ANYTHING,this.getType()))
            Unit
        else {
            newObject = this.clone(deep = false)
            newObject.setType(TypeUtils.makeMutable(this.getType()))
        }
        return newObject
    }

    override fun toString(): String {
        return when(value) {
            null -> "nothing"
            is Function<*, *> -> getType().components[0].toString()
            is MutableList<*> -> (value as MutableList<*>).joinToString(prefix = "[", postfix = "]")
            else -> value.toString()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjectDetails

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}
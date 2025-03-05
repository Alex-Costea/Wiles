package wiles.processor.types

import wiles.processor.utils.TypeUtils
import wiles.shared.errors.InternalErrorException

class EitherType(vararg typeList : AbstractType) : AbstractType(null) {


    private val subtypes : List<AbstractType>

    fun getSubtypes() : List<AbstractType>
    {
        return subtypes
    }

    init{
        val subtypesMutableList = mutableListOf<AbstractType>()
        for(type in typeList)
        {
            if(type is EitherType)
            {
                subtypesMutableList.addAll(type.subtypes)
            }
            else subtypesMutableList.add(type)
        }
        subtypes = subtypesMutableList.distinct()
    }

    override fun clone(value: Any?): AbstractType {
        if(value != null)
            throw InternalErrorException()
        val newSubtypes = subtypes.map { it.clone(it.exactValue) }
        return EitherType(*newSubtypes.toTypedArray())
    }

    override fun toString(): String {
        return subtypes.joinToString(" | ")
    }

    fun contains(type : AbstractType) : Boolean
    {
        for(subtype in subtypes) {
            if(TypeUtils.isSuperType(subtype, type))
                return true
        }
        return false
    }

}
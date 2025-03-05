package wiles.processor.types

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
        val newSubtypes = subtypes.map { it.exactly(it.exactValue) }
        return EitherType(*newSubtypes.toTypedArray())
    }

    override fun toString(): String {
        return subtypes.joinToString(" | ")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as EitherType

        return subtypes == other.subtypes
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + subtypes.hashCode()
        return result
    }


}
package wiles.processor.types

class EitherType(vararg typeList : AbstractType, exactly : Any? = null) : AbstractType(exactly) {
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
        //TODO: merge types
        subtypes = subtypesMutableList.distinct()
    }

    override fun clone(value: Any?): AbstractType {
        val newSubtypes = subtypes.map { it.exactly(it.exactValue) }
        return EitherType(*newSubtypes.toTypedArray(), exactly = value)
    }

    override fun toString(): String {
        return if(subtypes.size == 1 && subtypes[0].isExact())
            subtypes[0].toString()
        else if(exactValue != null)
            super.toString()
        else subtypes.joinToString(" | ")
    }

    override fun isExact(): Boolean {
        if(exactValue != null) return true
        if(subtypes.size != 1) return false
        return subtypes[0].isExact()
    }

    override fun getValue(): Any? {
        if(subtypes.size != 1) return null
        return subtypes[0].getValue()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as EitherType

        if (exactValue != other.exactValue) return false
        if (subtypes != other.subtypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (exactValue?.hashCode() ?: 0)
        result = 31 * result + subtypes.hashCode()
        return result
    }
}
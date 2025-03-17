package wiles.processor.types

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
        //TODO: merge types
        subtypes = subtypesMutableList.distinct()
    }

    override fun clone(value: Any?): AbstractType {
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

    override fun isExact(): Boolean {
        if(subtypes.size != 1) return false
        return subtypes[0].isExact()
    }

    override fun getValue(): Any? {
        if(subtypes.size != 1) return null
        return subtypes[0].getValue()
    }


}
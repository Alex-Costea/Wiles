package wiles.processor.types

import wiles.processor.utils.InterpreterUtils.isComponentSuperType

class WilesType(vararg typeList : AbstractType){

    private val typeList = minimize(typeList.distinct())

    override fun toString(): String {
        return typeList.joinToString(" | ")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WilesType

        return typeList == other.typeList
    }

    override fun hashCode(): Int {
        return typeList.hashCode()
    }

    fun getSubtypes(): List<AbstractType> {
        return typeList
    }

    fun removeExact(): WilesType {
        if(typeList.size == 1)
            return WilesType(typeList[0].ofValue(null))
        return this
    }

    fun getExactIfExists() : Any?
    {
        val expectedValues = getSubtypes().map { it.exactValue }.distinct()
        if(expectedValues.size == 1 && expectedValues[0] != null)
            return expectedValues[0]
        return null
    }

    companion object {
        fun minimize(list: List<AbstractType>): List<AbstractType> {
            val result = mutableListOf<AbstractType>()
            for (candidate in list) {
                if (result.any { isComponentSuperType(it, candidate) }) continue
                result.removeIf { isComponentSuperType(candidate, it) }
                result.add(candidate)
            }
            return result
        }
    }

}
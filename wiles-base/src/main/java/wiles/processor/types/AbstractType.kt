package wiles.processor.types


abstract class AbstractType(val exactValue : Any?) {

    open fun isExact(): Boolean {
        return exactValue != null
    }

    override fun toString(): String {
        return when {
            isExact() -> exactValue.toString()
            else -> this.javaClass.simpleName.substringBefore("Type")
        }
    }

    open fun getValue(): Any? {
        return exactValue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractType

        return exactValue == other.exactValue
    }

    override fun hashCode(): Int {
        val result = exactValue.hashCode()
        return result
    }

    abstract fun ofValue(obj : Any?): AbstractType

    companion object{
        val INT_TYPE = WilesType(IntType())
        val DECIMAL_TYPE = WilesType(DecimalType())
        val TEXT_TYPE = WilesType(TextType())
        val TYPE_TYPE = WilesType(TypeType())
        val NOTHING_TYPE = WilesType(NothingType())
        val ANYTHING_TYPE = WilesType(AnythingType())
        val TRUE_TYPE = WilesType(TrueType())
        val FALSE_TYPE = WilesType(FalseType())
        val TRUTH_TYPE = WilesType(TrueType(), FalseType())
        val INFINITY_TYPE = WilesType(InfinityType())
        val MINUS_INFINITY_TYPE = WilesType(MinusInfinityType())
        val FINITE_NUMBER_TYPE = WilesType(IntType(), DecimalType())
        val NUMBER_TYPE = WilesType(IntType(), DecimalType(), InfinityType(), MinusInfinityType())
        val INVALID_TYPE = WilesType(InvalidType())
    }

}

package wiles.processor.types


abstract class AbstractType(val exactValue : Any?) {

    fun isExact(): Boolean {
        return exactValue != null
    }

    override fun toString(): String {
        return when {
            isExact() && (this is TextType) -> "\"$exactValue\""
            isExact() -> exactValue.toString()
            else -> this.javaClass.simpleName.substringBefore("Type")
        }
    }

    fun getValue(): Any? {
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

    protected abstract fun clone(value: Any?) : AbstractType

    fun exactly(value: Any?) : AbstractType
    {
        return clone(value)
    }

    fun removeExact(): AbstractType {
        return clone(null)
    }

    companion object{
        val INTEGER_TYPE = IntType()
        val DECIMAL_TYPE = DecimalType()
        val TEXT_TYPE = TextType()
        val TYPE_TYPE = TypeType()
        val NOTHING_TYPE = NothingType()
        val BOOLEAN_TYPE = BooleanType()
        val ANYTHING_TYPE = AnythingType()
    }

}

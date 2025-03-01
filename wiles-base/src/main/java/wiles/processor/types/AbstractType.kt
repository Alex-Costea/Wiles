package wiles.processor.types

import wiles.processor.enums.WilesTypes

abstract class AbstractType(val exactValue : Any?) {
    abstract val typeName : WilesTypes

    fun isExact(): Boolean {
        return exactValue != null
    }

    override fun toString(): String {
        return when {
            isExact() && typeName == WilesTypes.Text -> "\"$exactValue\""
            isExact() -> exactValue.toString()
            else -> typeName.toString()
        }
    }

    fun getValue(): Any? {
        return exactValue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractType

        if (exactValue != other.exactValue) return false
        if (typeName != other.typeName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = exactValue.hashCode()
        result = 31 * result + typeName.hashCode()
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
        val INTEGER_TYPE = IntegerType()
        val DECIMAL_TYPE = DecimalType()
        val TEXT_TYPE = TextType()
        val TYPE_TYPE = TypeType()
        val NOTHING_TYPE = NothingType()
        val BOOLEAN_TYPE = BooleanType()
        val ANYTHING_TYPE = AnythingType()
    }

}

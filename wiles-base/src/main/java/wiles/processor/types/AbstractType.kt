package wiles.processor.types

import wiles.processor.enums.WilesTypes

abstract class AbstractType(val singletonValue : Any?) {
    abstract val typeName : WilesTypes

    fun isSingleton(): Boolean {
        return singletonValue != null
    }

    override fun toString(): String {
        var string = "$typeName"
        if(isSingleton())
            string += "($singletonValue)"
        return string
    }

    fun getValue(): Any? {
        return singletonValue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractType

        if (singletonValue != other.singletonValue) return false
        if (typeName != other.typeName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = singletonValue.hashCode()
        result = 31 * result + typeName.hashCode()
        return result
    }

    protected abstract fun init(singletonValue: Any?) : AbstractType

    fun singletonValueOf(singletonValue: Any?) : AbstractType
    {
        return init(singletonValue)
    }

    fun removeSingleton(): AbstractType {
        return init(null)
    }

    companion object{
        val INTEGER_TYPE = IntegerType()
        val DECIMAL_TYPE = DecimalType()
        val TEXT_TYPE = TextType()
        val TYPE_TYPE = TypeType()
    }

}

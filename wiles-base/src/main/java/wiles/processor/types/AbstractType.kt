package wiles.processor.types

import wiles.processor.enums.WilesTypes

abstract class AbstractType {
    private var singletonValue : Any? = null
    abstract val typeName : WilesTypes

    fun singletonValueOf(value : Any) : AbstractType{
        singletonValue = value
        return this
    }

    fun removeSingleton(): AbstractType {
        singletonValue = null
        return this
    }

    fun isSingleton(): Boolean {
        return singletonValue != null
    }

    override fun toString(): String {
        var string = "$typeName("
        if(isSingleton())
            string += "singletonValue=$singletonValue,"
        string += ")"
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

    fun clone() : AbstractType
    {
        val newType : AbstractType = when(this.typeName)
        {
            WilesTypes.INT -> IntegerType()
            WilesTypes.STRING -> StringType()
            WilesTypes.DECIMAL -> DecimalType()
            WilesTypes.INVALID -> InvalidType()
        }
        newType.singletonValue = this.singletonValue
        return  newType
    }

}

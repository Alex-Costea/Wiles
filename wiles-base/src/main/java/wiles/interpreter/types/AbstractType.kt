package wiles.interpreter.types

import wiles.interpreter.enums.WilesTypes

abstract class AbstractType {
    private var singletonValue : Any? = null
    private var isSingleton : Boolean = false
    abstract val typeName : WilesTypes

    fun singletonValueOf(value : Any) : AbstractType{
        isSingleton = true
        singletonValue = value
        return this
    }

    fun isSingleton(): Boolean {
        return isSingleton
    }

    override fun toString(): String {
        var string = "$typeName("
        if(isSingleton)
            string += "singletonValue=$singletonValue"
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

        if (isSingleton != other.isSingleton) return false
        if (singletonValue != other.singletonValue) return false
        if (typeName != other.typeName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isSingleton.hashCode()
        result = 31 * result + (singletonValue?.hashCode() ?: 0)
        result = 31 * result + typeName.hashCode()
        return result
    }
}

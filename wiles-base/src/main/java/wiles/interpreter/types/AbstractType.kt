package wiles.interpreter.types

import wiles.interpreter.WilesTypes

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
}

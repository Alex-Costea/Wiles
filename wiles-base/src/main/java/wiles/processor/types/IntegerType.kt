package wiles.processor.types

import wiles.processor.enums.WilesTypes

class IntegerType(singletonValue: Any? = null) : AbstractType(singletonValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.INT

    override fun init(singletonValue: Any?): AbstractType {
        return IntegerType(singletonValue)
    }
}
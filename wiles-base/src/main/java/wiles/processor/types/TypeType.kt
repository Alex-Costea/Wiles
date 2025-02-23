package wiles.processor.types

import wiles.processor.enums.WilesTypes

class TypeType(singletonValue: Any? = null) : AbstractType(singletonValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.TYPE

    override fun init(singletonValue: Any?): AbstractType {
        return TypeType(singletonValue)
    }
}
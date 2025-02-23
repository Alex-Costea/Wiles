package wiles.processor.types

import wiles.processor.enums.WilesTypes

class InvalidType(singletonValue: Any? = null) : AbstractType(singletonValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.INVALID

    override fun init(singletonValue: Any?): AbstractType {
        return InvalidType(singletonValue)
    }
}
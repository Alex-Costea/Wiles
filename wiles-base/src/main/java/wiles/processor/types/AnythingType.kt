package wiles.processor.types

import wiles.processor.enums.WilesTypes

class AnythingType(singletonValue: Any? = null) : AbstractType(singletonValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.ANYTHING

    override fun init(singletonValue: Any?): AbstractType {
        return AnythingType(singletonValue)
    }
}
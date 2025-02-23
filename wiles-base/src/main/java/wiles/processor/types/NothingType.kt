package wiles.processor.types

import wiles.processor.enums.WilesTypes

class NothingType(singletonValue: Any? = null) : AbstractType(singletonValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.NOTHING

    override fun init(singletonValue: Any?): AbstractType {
        return NothingType(singletonValue)
    }
}
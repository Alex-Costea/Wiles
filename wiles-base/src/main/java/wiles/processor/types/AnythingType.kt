package wiles.processor.types

import wiles.processor.enums.WilesTypes

class AnythingType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.ANYTHING

    override fun init(exactValue: Any?): AbstractType {
        return AnythingType(exactValue)
    }
}
package wiles.processor.types

import wiles.processor.enums.WilesTypes

class AnythingType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Anything

    override fun clone(value: Any?): AbstractType {
        return AnythingType(value)
    }
}
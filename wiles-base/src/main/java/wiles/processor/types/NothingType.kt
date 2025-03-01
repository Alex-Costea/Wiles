package wiles.processor.types

import wiles.processor.enums.WilesTypes

class NothingType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Nothing

    override fun clone(value: Any?): AbstractType {
        return NothingType(value)
    }
}
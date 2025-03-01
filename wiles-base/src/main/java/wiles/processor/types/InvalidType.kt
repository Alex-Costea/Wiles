package wiles.processor.types

import wiles.processor.enums.WilesTypes

class InvalidType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Invalid

    override fun clone(value: Any?): AbstractType {
        return InvalidType(value)
    }
}
package wiles.processor.types

import wiles.processor.enums.WilesTypes

class InvalidType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.INVALID

    override fun init(exactValue: Any?): AbstractType {
        return InvalidType(exactValue)
    }
}
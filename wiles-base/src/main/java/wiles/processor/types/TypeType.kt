package wiles.processor.types

import wiles.processor.enums.WilesTypes

class TypeType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.TYPE

    override fun init(exactValue: Any?): AbstractType {
        return TypeType(exactValue)
    }
}
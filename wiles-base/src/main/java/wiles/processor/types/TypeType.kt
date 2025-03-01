package wiles.processor.types

import wiles.processor.enums.WilesTypes

class TypeType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Type

    override fun clone(value: Any?): AbstractType {
        return TypeType(value)
    }
}
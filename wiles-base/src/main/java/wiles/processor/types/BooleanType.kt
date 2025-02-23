package wiles.processor.types

import wiles.processor.enums.WilesTypes

class BooleanType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.BOOLEAN

    override fun init(exactValue: Any?): AbstractType {
        return BooleanType(exactValue)
    }
}
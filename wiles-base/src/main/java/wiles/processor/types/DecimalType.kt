package wiles.processor.types

import wiles.processor.enums.WilesTypes

class DecimalType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.DECIMAL

    override fun init(exactValue: Any?): AbstractType {
        return DecimalType(exactValue)
    }
}
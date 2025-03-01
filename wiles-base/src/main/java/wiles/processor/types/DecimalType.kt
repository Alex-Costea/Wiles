package wiles.processor.types

import wiles.processor.enums.WilesTypes

class DecimalType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Decimal

    override fun clone(value: Any?): AbstractType {
        return DecimalType(value)
    }
}
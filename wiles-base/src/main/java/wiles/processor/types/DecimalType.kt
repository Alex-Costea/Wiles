package wiles.processor.types

import wiles.processor.enums.WilesTypes

class DecimalType(singletonValue: Any? = null) : AbstractType(singletonValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.DECIMAL

    override fun init(singletonValue: Any?): AbstractType {
        return DecimalType(singletonValue)
    }
}
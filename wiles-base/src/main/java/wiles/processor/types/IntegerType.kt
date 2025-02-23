package wiles.processor.types

import wiles.processor.enums.WilesTypes

class IntegerType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.INT

    override fun init(exactValue: Any?): AbstractType {
        return IntegerType(exactValue)
    }
}
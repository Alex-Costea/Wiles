package wiles.processor.types

import wiles.processor.enums.WilesTypes

class IntegerType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Int

    override fun init(exactValue: Any?): AbstractType {
        return IntegerType(exactValue)
    }
}
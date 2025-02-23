package wiles.processor.types

import wiles.processor.enums.WilesTypes

class BooleanType(singletonValue: Any? = null) : AbstractType(singletonValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.BOOLEAN

    override fun init(singletonValue: Any?): AbstractType {
        return BooleanType(singletonValue)
    }
}
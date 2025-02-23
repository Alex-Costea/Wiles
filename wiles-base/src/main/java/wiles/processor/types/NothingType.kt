package wiles.processor.types

import wiles.processor.enums.WilesTypes

class NothingType(exaxtValue: Any? = null) : AbstractType(exaxtValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Nothing

    override fun init(exactValue: Any?): AbstractType {
        return NothingType(exactValue)
    }
}
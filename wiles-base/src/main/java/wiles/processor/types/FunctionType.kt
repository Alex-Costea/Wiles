package wiles.processor.types

import wiles.processor.enums.WilesTypes

class FunctionType(exactValue: Any? = null,val yieldsType : AbstractType) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Function

    override fun init(exactValue: Any?): AbstractType {
        TODO()
    }
}
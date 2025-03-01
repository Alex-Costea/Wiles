package wiles.processor.types

import wiles.processor.enums.WilesTypes

class FunctionCallType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.FunctionCall

    override fun clone(value: Any?): AbstractType {
        return FunctionCallType(value)
    }
}
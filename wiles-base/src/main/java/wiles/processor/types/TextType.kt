package wiles.processor.types

import wiles.processor.enums.WilesTypes

class TextType(singletonValue: Any? = null) : AbstractType(singletonValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.TEXT

    override fun init(singletonValue: Any?): AbstractType {
        return TextType(singletonValue)
    }
}
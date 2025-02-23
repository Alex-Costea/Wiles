package wiles.processor.types

import wiles.processor.enums.WilesTypes

class TextType(exactValue: Any? = null) : AbstractType(exactValue) {
    override val typeName: WilesTypes
        get() = WilesTypes.Text

    override fun init(exactValue: Any?): AbstractType {
        return TextType(exactValue)
    }
}
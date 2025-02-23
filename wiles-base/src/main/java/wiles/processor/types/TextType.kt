package wiles.processor.types

import wiles.processor.enums.WilesTypes

class TextType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.TEXT
}
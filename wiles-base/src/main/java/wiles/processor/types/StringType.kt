package wiles.processor.types

import wiles.processor.enums.WilesTypes

class StringType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.STRING
}
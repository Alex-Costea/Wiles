package wiles.processor.types

import wiles.processor.enums.WilesTypes

class InvalidType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.INVALID
}
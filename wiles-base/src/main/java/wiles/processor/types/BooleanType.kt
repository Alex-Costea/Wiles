package wiles.processor.types

import wiles.processor.enums.WilesTypes

class BooleanType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.BOOLEAN
}
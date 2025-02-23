package wiles.processor.types

import wiles.processor.enums.WilesTypes

class AnythingType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.ANYTHING
}
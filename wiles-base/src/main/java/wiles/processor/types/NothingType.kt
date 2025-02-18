package wiles.processor.types

import wiles.processor.enums.WilesTypes

class NothingType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.NOTHING
}
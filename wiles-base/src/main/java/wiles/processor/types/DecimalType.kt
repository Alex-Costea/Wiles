package wiles.processor.types

import wiles.processor.enums.WilesTypes

class DecimalType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.DECIMAL
}
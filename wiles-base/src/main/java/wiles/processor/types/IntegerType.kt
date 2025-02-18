package wiles.processor.types

import wiles.processor.enums.WilesTypes

class IntegerType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.INT
}
package wiles.interpreter.types

import wiles.interpreter.enums.WilesTypes

class DecimalType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.DECIMAL
}
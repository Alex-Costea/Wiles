package wiles.interpreter.types

import wiles.interpreter.WilesTypes

class DecimalType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.DECIMAL
}
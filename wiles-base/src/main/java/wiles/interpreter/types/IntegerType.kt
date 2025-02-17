package wiles.interpreter.types

import wiles.interpreter.enums.WilesTypes

class IntegerType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.INT
}
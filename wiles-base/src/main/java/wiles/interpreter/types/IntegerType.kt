package wiles.interpreter.types

import wiles.interpreter.WilesTypes

class IntegerType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.INT
}
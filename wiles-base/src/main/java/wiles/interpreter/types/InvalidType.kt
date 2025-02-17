package wiles.interpreter.types

import wiles.interpreter.WilesTypes

class InvalidType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.INVALID
}
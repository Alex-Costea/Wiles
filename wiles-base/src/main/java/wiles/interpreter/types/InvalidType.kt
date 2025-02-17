package wiles.interpreter.types

import wiles.interpreter.enums.WilesTypes

class InvalidType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.INVALID
}
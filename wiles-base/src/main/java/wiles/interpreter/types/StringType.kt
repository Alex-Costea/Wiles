package wiles.interpreter.types

import wiles.interpreter.WilesTypes

class StringType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.STRING
}
package wiles.processor.types

import wiles.processor.enums.WilesTypes

class TypeType : AbstractType() {
    override val typeName: WilesTypes
        get() = WilesTypes.TYPE
}
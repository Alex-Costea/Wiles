package wiles.processor.types

import wiles.processor.enums.WilesTypes
import wiles.shared.errors.InternalErrorException

class NothingType : AbstractType(null) {
    override val typeName: WilesTypes
        get() = WilesTypes.Nothing

    override fun clone(value: Any?): AbstractType {
        if(value != null)
            throw InternalErrorException()
        return NothingType()
    }
}
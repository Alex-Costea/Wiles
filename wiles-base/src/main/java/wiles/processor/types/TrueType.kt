package wiles.processor.types

import wiles.shared.errors.InternalErrorException

class TrueType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        if(value != null)
            throw InternalErrorException()
        return TrueType()
    }

    override fun toString(): String {
        return "true"
    }
}
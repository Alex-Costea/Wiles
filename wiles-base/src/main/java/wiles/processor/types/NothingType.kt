package wiles.processor.types

import wiles.shared.errors.InternalErrorException

class NothingType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        if(value != null)
            throw InternalErrorException()
        return NothingType()
    }

    override fun toString(): String {
        return "nothing"
    }
}
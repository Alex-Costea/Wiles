package wiles.processor.types

import wiles.shared.errors.InternalErrorException

class TypeType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        if(value != null)
            throw InternalErrorException()
        return TypeType()
    }

    override fun toString(): String {
        return "Type"
    }
}
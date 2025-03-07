package wiles.processor.types

import wiles.shared.errors.InternalErrorException

class FalseType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        if(value != null)
            throw InternalErrorException()
        return FalseType()
    }
}
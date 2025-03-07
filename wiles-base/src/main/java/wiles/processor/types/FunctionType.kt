package wiles.processor.types

import wiles.shared.errors.InternalErrorException

class FunctionType(exactValue: Any? = null, val yieldsType : AbstractType) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        if(value != null)
            throw InternalErrorException()
        return FunctionType(value, yieldsType)
    }
}
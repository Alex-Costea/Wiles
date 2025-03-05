package wiles.processor.types

class FunctionCallType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return FunctionCallType(value)
    }
}
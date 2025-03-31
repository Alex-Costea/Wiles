package wiles.processor.types

class FunctionCallType : AbstractType(null) {
    override fun ofValue(obj : Any?): AbstractType {
        return FunctionCallType()
    }
}
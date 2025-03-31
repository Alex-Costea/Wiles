package wiles.processor.types

class FunctionCallType() : AbstractType(null) {
    override fun removeExact(): AbstractType {
        return FunctionCallType()
    }
}
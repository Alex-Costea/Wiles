package wiles.processor.types

class IntType(exactValue: Any? = null) : AbstractType(exactValue) {
    override fun removeExact(): AbstractType {
        return IntType()
    }
}
package wiles.processor.types

class IntType(exactValue: Any? = null) : AbstractType(exactValue) {
    override fun ofValue(obj : Any?): AbstractType {
        return IntType(obj)
    }
}
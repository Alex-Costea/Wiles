package wiles.processor.types

class IntType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return IntType(value)
    }
}
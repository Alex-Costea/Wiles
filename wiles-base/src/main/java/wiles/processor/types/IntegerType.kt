package wiles.processor.types

class IntegerType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return IntegerType(value)
    }
}
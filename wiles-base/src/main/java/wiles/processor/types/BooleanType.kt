package wiles.processor.types


class BooleanType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return BooleanType(value)
    }
}
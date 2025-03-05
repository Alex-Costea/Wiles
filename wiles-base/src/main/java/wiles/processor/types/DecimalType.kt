package wiles.processor.types

class DecimalType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return DecimalType(value)
    }
}
package wiles.processor.types

class DecimalType(exactValue: Any? = null) : AbstractType(exactValue) {
    override fun removeExact(): AbstractType {
        return DecimalType()
    }
}
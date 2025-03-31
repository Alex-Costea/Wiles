package wiles.processor.types

class DecimalType(exactValue: Any? = null) : AbstractType(exactValue) {
    override fun ofValue(obj : Any?): AbstractType {
        return DecimalType(obj)
    }
}
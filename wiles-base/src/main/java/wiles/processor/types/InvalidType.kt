package wiles.processor.types

class InvalidType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return InvalidType()
    }
}
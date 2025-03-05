package wiles.processor.types

class TypeType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return TypeType(value)
    }
}
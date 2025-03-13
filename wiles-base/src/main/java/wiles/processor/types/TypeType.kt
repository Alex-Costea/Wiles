package wiles.processor.types

class TypeType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        return TypeType()
    }

    override fun toString(): String {
        return "Type"
    }
}
package wiles.processor.types

class TypeType : AbstractType(null) {

    override fun toString(): String {
        return "Type"
    }

    override fun removeExact(): AbstractType {
        return TypeType()
    }
}
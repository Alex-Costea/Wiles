package wiles.processor.types

class TypeType : AbstractType(null) {
    //TODO: add subtype

    override fun toString(): String {
        return "Type"
    }

    override fun ofValue(obj : Any?): AbstractType {
        return TypeType()
    }
}
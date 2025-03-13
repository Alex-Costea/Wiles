package wiles.processor.types

class TrueType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        return TrueType()
    }

    override fun toString(): String {
        return "true"
    }
}
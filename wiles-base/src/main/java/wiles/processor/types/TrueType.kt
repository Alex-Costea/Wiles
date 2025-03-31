package wiles.processor.types

class TrueType : AbstractType(null) {

    override fun toString(): String {
        return "true"
    }

    override fun ofValue(obj : Any?): AbstractType {
        return TrueType()
    }
}
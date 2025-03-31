package wiles.processor.types

class MinusInfinityType : AbstractType(null) {

    override fun toString(): String {
        return "-infinity"
    }

    override fun ofValue(obj : Any?): AbstractType {
        return MinusInfinityType()
    }
}
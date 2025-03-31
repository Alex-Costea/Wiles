package wiles.processor.types

class MinusInfinityType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        return MinusInfinityType()
    }

    override fun toString(): String {
        return "-infinity"
    }
}
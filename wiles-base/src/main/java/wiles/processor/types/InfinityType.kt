package wiles.processor.types

class InfinityType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        return InfinityType()
    }

    override fun toString(): String {
        return "infinity"
    }
}
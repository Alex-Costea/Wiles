package wiles.processor.types

class InfinityType : AbstractType(null) {

    override fun toString(): String {
        return "infinity"
    }

    override fun ofValue(obj : Any?): AbstractType {
        return InfinityType()
    }
}
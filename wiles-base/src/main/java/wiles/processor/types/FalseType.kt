package wiles.processor.types

class FalseType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        return FalseType()
    }

    override fun toString(): String {
        return "false"
    }
}
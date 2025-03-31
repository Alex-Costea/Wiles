package wiles.processor.types

class FalseType : AbstractType(null) {

    override fun toString(): String {
        return "false"
    }

    override fun ofValue(obj : Any?): AbstractType {
        return FalseType()
    }
}
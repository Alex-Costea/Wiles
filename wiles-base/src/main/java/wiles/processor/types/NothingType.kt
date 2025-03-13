package wiles.processor.types

class NothingType : AbstractType(null) {

    override fun clone(value: Any?): AbstractType {
        return NothingType()
    }

    override fun toString(): String {
        return "nothing"
    }
}
package wiles.processor.types

class NothingType : AbstractType(null) {

    override fun toString(): String {
        return "nothing"
    }

    override fun removeExact(): AbstractType {
        return NothingType()
    }
}
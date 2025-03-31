package wiles.processor.types

class InvalidType() : AbstractType(null) {
    override fun removeExact(): AbstractType {
        return InvalidType()
    }

}
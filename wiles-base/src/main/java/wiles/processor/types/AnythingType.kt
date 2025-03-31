package wiles.processor.types


class AnythingType() : AbstractType(null) {
    override fun removeExact(): AbstractType {
        return AnythingType()
    }
}
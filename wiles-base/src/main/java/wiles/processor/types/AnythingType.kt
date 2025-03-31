package wiles.processor.types


class AnythingType : AbstractType(null) {
    override fun ofValue(obj : Any?): AbstractType {
        return AnythingType()
    }
}
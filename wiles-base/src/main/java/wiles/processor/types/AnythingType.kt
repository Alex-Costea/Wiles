package wiles.processor.types


class AnythingType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return AnythingType()
    }
}
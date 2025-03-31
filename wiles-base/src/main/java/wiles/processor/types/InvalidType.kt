package wiles.processor.types

class InvalidType : AbstractType(null) {
    override fun ofValue(obj : Any?): AbstractType {
        return InvalidType()
    }

}
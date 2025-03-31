package wiles.processor.types

class DataType : AbstractType(null) {
    override fun ofValue(obj : Any?): AbstractType {
        return DataType()
    }
}
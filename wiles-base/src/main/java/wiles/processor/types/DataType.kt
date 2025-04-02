package wiles.processor.types

class DataType(obj: Any?) : AbstractType(obj) {
    override fun ofValue(obj : Any?): AbstractType {
        return DataType(obj)
    }
}
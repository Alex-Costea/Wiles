package wiles.processor.types

class FunctionType(exactValue: Any? = null, val yieldsType : WilesType) : AbstractType(exactValue) {

    override fun toString(): String {
        return "fun() -> $yieldsType"
    }

    override fun removeExact(): AbstractType {
        return FunctionType(null, yieldsType)
    }
}
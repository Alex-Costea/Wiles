package wiles.processor.types

class FunctionType(exactValue: Any? = null, val yieldsType : AbstractType) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return FunctionType(value, yieldsType)
    }

    override fun toString(): String {
        return "fun() -> $yieldsType"
    }
}
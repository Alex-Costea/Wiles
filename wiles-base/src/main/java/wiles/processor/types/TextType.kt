package wiles.processor.types

class TextType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun clone(value: Any?): AbstractType {
        return TextType(value)
    }
}
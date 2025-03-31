package wiles.processor.types

class TextType(exactValue: Any? = null) : AbstractType(exactValue) {

    override fun toString(): String {
        if(isExact()) return "\"$exactValue\""
        return super.toString()
    }

    override fun removeExact(): AbstractType {
        return TextType()
    }
}
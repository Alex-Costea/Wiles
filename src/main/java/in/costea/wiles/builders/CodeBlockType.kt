package `in`.costea.wiles.builders

class CodeBlockType {
    var isOutermost = false
    private set
    var isWithinMethod = false
    private set
    fun outermost() : CodeBlockType
    {
        isOutermost = true
        return this
    }

    fun withinMethod() : CodeBlockType
    {
        isWithinMethod = true
        return this
    }
}
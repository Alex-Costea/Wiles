package wiles.interpreter

class Value(
    private var obj: Any,
    private val type : WilesType
) {
    fun getObj() : Any{
        return obj
    }

    override fun toString(): String {
        return "Value(obj=$obj, type=$type)"
    }

}
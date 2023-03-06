package wiles.interpreter.data

class VariableMap : HashMap<String, Long>()
{
    override fun clone(): VariableMap {
        val newMap = VariableMap()
        newMap.putAll(this)
        return newMap
    }
}
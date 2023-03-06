package wiles.interpreter.data

class VariableMap : HashMap<String, ObjectDetails>()
{
    override fun clone(): VariableMap {
        val newMap = VariableMap()
        newMap.putAll(this)
        return newMap
    }
}
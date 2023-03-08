package wiles.interpreter.data

class VariableMap : HashMap<String, ObjectDetails>()
{
    fun copy(): VariableMap {
        val newMap = VariableMap()
        newMap.putAll(this)
        return newMap
    }
}

package wiles.interpreter.data

class VariableMap : HashMap<String, VariableDetails>()
{
    override fun clone() : VariableMap
    {
        val newMap = VariableMap()
        newMap.putAll(this.map { Pair(it.key, it.value.clone()) })
        return newMap
    }
}
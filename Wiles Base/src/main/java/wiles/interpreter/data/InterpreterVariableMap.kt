package wiles.interpreter.data

class InterpreterVariableMap : HashMap<String, ObjectDetails>()
{
    fun copy(): InterpreterVariableMap {
        val newMap = InterpreterVariableMap()
        newMap.putAll(this)
        return newMap
    }
}

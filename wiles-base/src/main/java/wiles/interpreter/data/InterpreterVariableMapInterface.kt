package wiles.interpreter.data

interface InterpreterVariableMapInterface{
    operator fun set(name: String, value: ObjectDetails)

    operator fun get(name: String): ObjectDetails?

    fun copy(): InterpreterVariableMapInterface
    fun containsKey(key: String): Boolean
    fun putAll(newMap: List<Pair<String, ObjectDetails>>)
    fun filter(predicate: (Map.Entry<String, ObjectDetails>) -> Boolean): List<Pair<String, ObjectDetails>>

    fun declare(name : String, value : ObjectDetails)
}
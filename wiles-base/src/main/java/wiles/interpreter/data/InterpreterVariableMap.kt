package wiles.interpreter.data

class InterpreterVariableMap : InterpreterVariableMapInterface
{
    val map = HashMap<String, ObjectDetails>()
    override fun copy(): InterpreterVariableMap {
        val newMap = InterpreterVariableMap()
        newMap.putAll(map.toList())
        return newMap
    }

    override fun containsKey(key: String): Boolean {
        return map.containsKey(key)
    }

    override fun putAll(newMap: List<Pair<String, ObjectDetails>>) {
        map.putAll(newMap)
    }

    override fun filter(predicate: (Map.Entry<String, ObjectDetails>) -> Boolean): List<Pair<String, ObjectDetails>> {
        return map.filter(predicate).toList()
    }

    override fun declare(name: String, value: ObjectDetails) {
        this[name] = value
    }

    fun putAll(newMap: InterpreterVariableMap) {
        map.putAll(newMap.map)
    }

    override fun set(name: String, value: ObjectDetails) {
        map[name] = value
    }

    override fun get(name: String): ObjectDetails? {
        return map[name]
    }

    fun display(): List<String> {
        return map.map { it.key + " -> " + it.value}
    }
}

package wiles.interpreter.data

class ComplexInterpreterVariableMap
    (private val innerMap : InterpreterVariableMapInterface, private val outerMap : InterpreterVariableMapInterface)
    : InterpreterVariableMapInterface {

    override fun putAll(newMap: List<Pair<String, ObjectDetails>>) {
        outerMap.putAll(newMap)
    }

    override fun filter(predicate: (Map.Entry<String, ObjectDetails>) -> Boolean): List<Pair<String, ObjectDetails>> {
        val list1 = innerMap.filter(predicate)
        val list2 = outerMap.filter(predicate)
        return list1 + list2
    }

    override fun declare(name: String, value: ObjectDetails) {
        innerMap[name] = value
    }

    override fun set(name: String, value: ObjectDetails) {
        if(innerMap.containsKey(name))
            innerMap[name] = value
        else outerMap[name] = value
    }

    override fun get(name: String): ObjectDetails? {
        return if(innerMap.containsKey(name))
            innerMap[name]
        else outerMap[name]
    }

    override fun copy(): InterpreterVariableMapInterface {
        val complexInterpreterVariableMap = ComplexInterpreterVariableMap(innerMap.copy(), outerMap.copy())
        return complexInterpreterVariableMap
    }

    override fun containsKey(key: String): Boolean {
        if(innerMap.containsKey(key))
            return true
        if(outerMap.containsKey(key))
            return true
        return false
    }
}
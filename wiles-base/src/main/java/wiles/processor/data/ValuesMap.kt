package wiles.processor.data

class ValuesMap : Map<String, Value>{
    private val linkedHashMap = linkedMapOf<String, Value>()

    fun putAll(from: Map<out String, Value>) {
        linkedHashMap.putAll(from)
    }

    fun putAll(map: ValuesMap) {
        linkedHashMap.putAll(map.linkedHashMap)
    }

    fun filter(function: (Map.Entry<String, Value>) -> Boolean): Map<String, Value> {
        return linkedHashMap.filter(function)
    }

    override operator fun get(key: String): Value? {
        return linkedHashMap[key]
    }

    operator fun set(name: String, value: Value) {
        linkedHashMap[name] = value
    }

    override fun containsKey(key: String): Boolean {
        return linkedHashMap.containsKey(key)
    }

    override val entries: Set<Map.Entry<String, Value>>
        get() = linkedHashMap.entries
    override val keys: Set<String>
        get() = linkedHashMap.keys
    override val size: Int
        get() = linkedHashMap.size
    override val values: Collection<Value>
        get() = linkedHashMap.values

    override fun isEmpty(): Boolean {
        return linkedHashMap.isEmpty()
    }

    override fun containsValue(value: Value): Boolean {
        return linkedHashMap.containsValue(value)
    }
}
package wiles.processor.data

class ValuesMap(map: Map<String, ValueData>? = null) : Map<String, ValueData>{
    private val linkedHashMap = map?.let { LinkedHashMap(it) } ?: linkedMapOf<String, ValueData>()

    override fun toString(): String {
        return linkedHashMap.toString()
    }

    fun putAll(map: ValuesMap) {
        linkedHashMap.putAll(map.linkedHashMap)
    }

    fun clear()
    {
        linkedHashMap.clear()
    }

    fun filter(function: (Map.Entry<String, ValueData>) -> Boolean): ValuesMap {
        return ValuesMap(linkedHashMap.filter(function))
    }

    override operator fun get(key: String): ValueData? {
        return linkedHashMap[key]
    }

    operator fun set(name: String, value: ValueData) {
        linkedHashMap[name] = value
    }

    override fun containsKey(key: String): Boolean {
        return linkedHashMap.containsKey(key)
    }

    override val entries: Set<Map.Entry<String, ValueData>>
        get() = linkedHashMap.entries
    override val keys: Set<String>
        get() = linkedHashMap.keys
    override val size: Int
        get() = linkedHashMap.size
    override val values: Collection<ValueData>
        get() = linkedHashMap.values

    override fun isEmpty(): Boolean {
        return linkedHashMap.isEmpty()
    }

    override fun containsValue(value: ValueData): Boolean {
        return linkedHashMap.containsValue(value)
    }
}
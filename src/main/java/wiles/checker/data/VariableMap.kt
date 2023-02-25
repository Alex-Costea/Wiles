package wiles.checker.data

class VariableMap(map : HashMap<String, VariableDetails>) : HashMap<String, VariableDetails>(map) {
    fun copy() : VariableMap
    {
        val newMap = VariableMap()
        for(component in this.entries)
        {
            newMap[component.component1()] = component.component2().copy()
        }
        return newMap
    }

    constructor() : this(hashMapOf())
}
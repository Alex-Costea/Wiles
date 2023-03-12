package wiles.checker.data

class CheckerVariableMap(map : HashMap<String, VariableDetails>) : HashMap<String, VariableDetails>(map) {
    fun copy() : CheckerVariableMap
    {
        val newMap = CheckerVariableMap()
        for(component in this.entries)
        {
            newMap[component.component1()] = component.component2().copy()
        }
        return newMap
    }

    constructor() : this(hashMapOf())
}
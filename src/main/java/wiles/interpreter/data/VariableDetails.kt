package wiles.interpreter.data

import wiles.shared.JSONStatement

data class VariableDetails(val reference : Long, val type : JSONStatement) {
    fun clone(): VariableDetails {
        return VariableDetails(reference, type.copyRemovingLocation())
    }
}
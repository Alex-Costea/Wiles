package wiles.checker.data

import wiles.shared.JSONStatement

data class VariableDetails(
    val type : JSONStatement,
    var initialized: Boolean = true,
    val modifiable : Boolean = false,
)
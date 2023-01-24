package wiles.checker

import wiles.shared.JSONStatement

data class VariableDetails(
    val type : JSONStatement,
    val initialized: Boolean = true,
    val modifiable : Boolean = false,
)

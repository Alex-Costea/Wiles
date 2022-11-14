package wiles.checker

data class IdentifierDetails(
    val type : TypeDefinition,
    var isInit : Boolean = true,
    val isVar : Boolean = false
)

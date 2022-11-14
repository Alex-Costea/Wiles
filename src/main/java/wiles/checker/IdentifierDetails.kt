package wiles.checker

data class IdentifierDetails(
    val type : String,
    var isInit : Boolean = true,
    val isVar : Boolean = false
)

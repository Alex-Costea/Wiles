package wiles.checker

data class IdentifierDetails(
    //TODO: type should be more than String
    val type : String,
    var isInit : Boolean = true,
    val isVar : Boolean = false
)

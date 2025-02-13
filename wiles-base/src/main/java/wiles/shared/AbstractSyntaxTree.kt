package wiles.shared

data class AbstractSyntaxTree(
    val name : String,
    val syntaxType: SyntaxType,
    val location: TokenLocation,
    val components : List<AbstractSyntaxTree>
)

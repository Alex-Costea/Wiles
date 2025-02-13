package wiles.shared

data class AbstractSyntaxTree(
    val details : List<String>,
    val syntaxType: SyntaxType,
    val location: TokenLocation?,
    val components : List<AbstractSyntaxTree>
)

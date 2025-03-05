package wiles.shared

interface StatementInterface : LocationAccessibleInterface{
    override val location : TokenLocation?

    override fun getComponents(): MutableList<out StatementInterface>

    var name: String

    val syntaxType: SyntaxType?
}
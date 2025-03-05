package wiles.shared.abstracts

import wiles.shared.data.TokenLocation
import wiles.shared.enums.SyntaxType

interface StatementInterface : LocationAccessibleInterface {
    override val location : TokenLocation?

    override fun getComponents(): MutableList<out StatementInterface>

    var name: String

    val syntaxType: SyntaxType?
}
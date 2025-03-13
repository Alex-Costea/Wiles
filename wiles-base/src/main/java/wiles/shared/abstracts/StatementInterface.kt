package wiles.shared.abstracts

import wiles.shared.data.TokenLocation
import wiles.shared.enums.SyntaxType

interface StatementInterface : SharedStatementInterface {
    override val location : TokenLocation?

    override fun getComponents(): MutableList<out StatementInterface>

    var name: String

    override val syntaxType: SyntaxType?
}
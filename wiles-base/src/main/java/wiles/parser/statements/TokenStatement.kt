package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.shared.abstracts.AbstractStatement
import wiles.shared.data.WilesExceptionsCollection
import wiles.shared.data.Token
import wiles.shared.enums.SyntaxType

class TokenStatement(val token: Token, context : ParserContext) : AbstractStatement(context) {
    init {
        name = token.content
        location = token.location
    }

    override val syntaxType: SyntaxType
        get() = SyntaxType.TOKEN

    override fun getComponents(): MutableList<AbstractStatement> {
        return ArrayList()
    }

    override fun process(): WilesExceptionsCollection {
        return WilesExceptionsCollection()
    }
}
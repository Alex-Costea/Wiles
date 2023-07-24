package wiles.parser.statements

import wiles.parser.builders.ParserContext
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.Token
import wiles.shared.SyntaxType

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

    override fun process(): CompilationExceptionsCollection {
        return CompilationExceptionsCollection()
    }
}
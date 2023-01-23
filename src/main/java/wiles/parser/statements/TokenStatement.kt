package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.Token
import wiles.shared.SyntaxType

class TokenStatement(val token: Token, context : Context) : AbstractStatement(context) {
    init {
        name = token.content
        location = token.location.toString()
    }

    override val type: SyntaxType
        get() = SyntaxType.TOKEN

    override fun getComponents(): List<AbstractStatement> {
        return ArrayList()
    }

    override fun process(): CompilationExceptionsCollection {
        return CompilationExceptionsCollection()
    }
}
package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.data.Token
import wiles.parser.enums.SyntaxType

class TokenStatement(val token: Token, context : Context) : AbstractStatement(context) {
    init {
        name = token.content
    }

    override val type: SyntaxType
        get() = SyntaxType.TOKEN

    override fun getComponents(): List<AbstractStatement> {
        return ArrayList()
    }

    override fun process(): CompilationExceptionsCollection {
        return CompilationExceptionsCollection()
    }

    override fun toString(): String {
        return name
    }
}
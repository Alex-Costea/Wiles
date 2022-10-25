package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.enums.SyntaxType

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
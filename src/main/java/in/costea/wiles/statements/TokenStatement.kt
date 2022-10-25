package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.IsWithin
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.services.TokenTransmitter

class TokenStatement(transmitter: TokenTransmitter, val token: Token,within : IsWithin) : AbstractStatement(transmitter,within) {
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
package `in`.costea.wiles.commands

import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.services.TokenTransmitter
import org.apache.commons.lang3.StringEscapeUtils

class TokenCommand(transmitter: TokenTransmitter, val token: Token) : AbstractCommand(transmitter) {

    init {
        name = token.content
    }

    override val type: SyntaxType
        get() = SyntaxType.TOKEN

    override fun getComponents(): List<AbstractCommand> {
        return ArrayList()
    }

    override fun process(): CompilationExceptionsCollection {
        return CompilationExceptionsCollection()
    }

    override fun toString(): String {
        return StringEscapeUtils.escapeJava(name)
    }
}
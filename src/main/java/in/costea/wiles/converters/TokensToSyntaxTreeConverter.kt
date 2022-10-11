package `in`.costea.wiles.converters

import `in`.costea.wiles.commands.ProgramCommand
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.services.TokenTransmitter

class TokensToSyntaxTreeConverter(tokens: List<Token>) {
    val exceptions: CompilationExceptionsCollection
    private val tokenTransmitter: TokenTransmitter

    init {
        tokenTransmitter = TokenTransmitter(tokens)
        exceptions = CompilationExceptionsCollection()
    }

    fun convert(): ProgramCommand {
        val syntaxTree = ProgramCommand(tokenTransmitter)
        exceptions.addAll(syntaxTree.process())
        return syntaxTree
    }
}
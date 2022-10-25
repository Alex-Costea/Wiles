package `in`.costea.wiles.converters

import `in`.costea.wiles.statements.CodeBlockStatement
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.data.Token
import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.data.TokenLocation
import `in`.costea.wiles.services.TokenTransmitter

class TokensToSyntaxTreeConverter(tokens: List<Token>, lastLocation : TokenLocation) {
    val exceptions: CompilationExceptionsCollection
    private val tokenTransmitter: TokenTransmitter

    init {
        tokenTransmitter = TokenTransmitter(tokens, lastLocation)
        exceptions = CompilationExceptionsCollection()
    }

    fun convert(): CodeBlockStatement {
        val syntaxTree = CodeBlockStatement(Context(tokenTransmitter).setOutermost())
        exceptions.addAll(syntaxTree.process())
        return syntaxTree
    }
}
package wiles.parser.converters

import wiles.parser.statements.CodeBlockStatement
import wiles.shared.CompilationExceptionsCollection
import wiles.parser.data.Token
import wiles.parser.builders.Context
import wiles.parser.data.TokenLocation
import wiles.parser.services.TokenTransmitter

class TokensToSyntaxTreeConverter(tokens: List<Token>, lastLocation : TokenLocation) {
    val exceptions: CompilationExceptionsCollection
    private val tokenTransmitter: TokenTransmitter

    init {
        tokenTransmitter = TokenTransmitter(tokens, lastLocation)
        exceptions = CompilationExceptionsCollection()
    }

    fun convert(): CodeBlockStatement {
        val syntaxTree = CodeBlockStatement(Context(tokenTransmitter).setOutermost(true))
        exceptions.addAll(syntaxTree.process())
        return syntaxTree
    }
}
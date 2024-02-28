package wiles.parser.converters

import wiles.parser.statements.CodeBlockStatement
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.Token
import wiles.parser.builders.ParserContext
import wiles.shared.TokenLocation
import wiles.parser.services.TokenTransmitter

class TokensToSyntaxTreeConverter(tokens: List<Token>, lastLocation : TokenLocation) {
    val exceptions: CompilationExceptionsCollection
    private val tokenTransmitter: TokenTransmitter

    init {
        tokenTransmitter = TokenTransmitter(tokens, lastLocation)
        exceptions = CompilationExceptionsCollection()
    }

    fun convert(): CodeBlockStatement {
        val syntaxTree = CodeBlockStatement(ParserContext(tokenTransmitter).setOutermost(true))
        exceptions.addAll(syntaxTree.process())
        return syntaxTree
    }
}
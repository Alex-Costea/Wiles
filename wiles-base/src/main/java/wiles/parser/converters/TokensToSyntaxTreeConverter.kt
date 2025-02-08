package wiles.parser.converters

import wiles.parser.builders.ParserContext
import wiles.parser.services.TokenTransmitter
import wiles.parser.statements.CodeBlockStatement
import wiles.parser.statements.ProgramStatement
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.Token
import wiles.shared.TokenLocation

class TokensToSyntaxTreeConverter(tokens: List<Token>, lastLocation : TokenLocation) {
    val exceptions = CompilationExceptionsCollection()
    private val tokenTransmitter = TokenTransmitter(tokens, lastLocation)

    fun convert(): CodeBlockStatement {
        val syntaxTree = ProgramStatement(ParserContext(tokenTransmitter))
        exceptions.addAll(syntaxTree.process())
        return syntaxTree
    }
}
package wiles.parser.converters

import wiles.parser.builders.ParserContext
import wiles.parser.services.TokenTransmitter
import wiles.parser.statements.CodeBlockStatement
import wiles.parser.statements.ProgramStatement
import wiles.shared.data.WilesExceptionsCollection
import wiles.shared.data.Token
import wiles.shared.data.TokenLocation

class TokensToSyntaxTreeConverter(tokens: List<Token>, lastLocation : TokenLocation) {
    val exceptions = WilesExceptionsCollection()
    private val tokenTransmitter = TokenTransmitter(tokens, lastLocation)

    fun convert(): CodeBlockStatement {
        val syntaxTree = ProgramStatement(ParserContext(tokenTransmitter))
        exceptions.addAll(syntaxTree.process())
        return syntaxTree
    }
}
package wiles.parser.statements.expressions

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.exceptions.TokenExpectedException
import wiles.parser.exceptions.UnexpectedEndException
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages.INTERNAL_ERROR
import wiles.shared.constants.Tokens.PAREN_END_ID

class InnerExpression(oldContext: ParserContext) : AbstractExpression(oldContext) {
    @Throws(TokenExpectedException::class, UnexpectedEndException::class)
    override fun handleToken(token: Token): Boolean {
        if (token.content == PAREN_END_ID) {
            transmitter.expect(tokenOf(PAREN_END_ID).withErrorMessage(INTERNAL_ERROR))
            return true
        }
        return false
    }
}

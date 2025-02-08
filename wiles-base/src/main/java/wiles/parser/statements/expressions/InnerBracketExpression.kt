package wiles.parser.statements.expressions

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.exceptions.TokenExpectedException
import wiles.parser.exceptions.UnexpectedEndException
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages.INTERNAL_ERROR
import wiles.shared.constants.Tokens.BRACKET_END_ID

class InnerBracketExpression(oldContext: ParserContext) : AbstractExpression(oldContext.setWithinInnerExpression(true)) {
    @Throws(TokenExpectedException::class, UnexpectedEndException::class)
    override fun handleToken(token: Token): Boolean {
        if (token.content == BRACKET_END_ID) {
            transmitter.expect(tokenOf(BRACKET_END_ID).withErrorMessage(INTERNAL_ERROR))
            return true
        }
        return false
    }
}

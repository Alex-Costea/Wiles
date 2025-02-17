package wiles.parser.statements.expressions

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.services.PrecedenceProcessor
import wiles.parser.statements.TokenStatement
import wiles.shared.WilesException
import wiles.shared.Token
import wiles.shared.constants.Tokens.ASSIGN_ID

class InsideMethodCallExpression(oldContext: ParserContext) : AbstractExpression(oldContext) {
    private var isAssignment: Boolean = false

    override fun setComponents(precedenceProcessor: PrecedenceProcessor) {
        if (isAssignment) this.left = precedenceProcessor.getResult()
        else super.setComponents(precedenceProcessor)
    }

    @Throws(WilesException::class)
    override fun handleToken(token: Token): Boolean {
        if (token.content == ASSIGN_ID) {
            operation = TokenStatement(transmitter.expect(tokenOf(ASSIGN_ID)), context)
            val newRight = DefaultExpression(context)
            exceptions.addAll(newRight.process())
            right = newRight
            isAssignment = true
            return true
        }
        return super.handleToken(token)
    }
}

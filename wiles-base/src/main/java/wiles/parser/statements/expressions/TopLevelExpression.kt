package wiles.parser.statements.expressions

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.services.PrecedenceProcessor
import wiles.parser.statements.TokenStatement
import wiles.shared.AbstractCompilationException
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages.INTERNAL_ERROR
import wiles.shared.constants.Tokens.ASSIGN_ID

class TopLevelExpression(context: ParserContext) : AbstractExpression(context) {
    var isAssignment: Boolean = false

    override fun setComponents(precedenceProcessor: PrecedenceProcessor) {
        if (isAssignment) this.left = precedenceProcessor.getResult()
        else super.setComponents(precedenceProcessor)
    }

    @Throws(AbstractCompilationException::class)
    override fun handleToken(token: Token): Boolean {
        val content = token.content
        if (content == ASSIGN_ID) {
            operation = TokenStatement(
                transmitter.expect(
                    tokenOf(ASSIGN_ID).withErrorMessage(INTERNAL_ERROR)
                ), context
            )
            val newRight = DefaultExpression(context)
            exceptions.addAll(newRight.process())
            right = newRight
            isAssignment = true
            return true
        }
        return super.handleToken(token)
    }
}

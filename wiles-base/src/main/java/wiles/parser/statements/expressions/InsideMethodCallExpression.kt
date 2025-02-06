package wiles.parser.statements.expressions

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.services.PrecedenceProcessor
import wiles.parser.statements.TokenStatement
import wiles.shared.AbstractCompilationException
import wiles.shared.Token
import wiles.shared.constants.Tokens.ASSIGN_ID

class InsideMethodCallExpression(oldContext: ParserContext) :
    AbstractExpression(oldContext.setWithinInnerExpression(true)) {
    protected var isAssignment: Boolean = false

    init {
        isInner = true
    }


    override fun setComponents(precedenceProcessor: PrecedenceProcessor) {
        if (isAssignment) this.left = precedenceProcessor.getResult()
        else super.setComponents(precedenceProcessor)
    }

    @Throws(AbstractCompilationException::class)
    override fun handleToken(token: Token): Boolean {
        if (token.content == ASSIGN_ID) {
            operation = TokenStatement(transmitter.expect(tokenOf(ASSIGN_ID)), context)
            val new_right = InnerDefaultExpression(context)
            exceptions.addAll(new_right.process())
            right = new_right
            isAssignment = true
            return true
        }
        return super.handleToken(token)
    }
}

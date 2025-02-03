package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder
import wiles.parser.builders.ParserContext
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.statements.expressions.InnerDefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.Token
import wiles.shared.constants.ErrorMessages
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens
import wiles.shared.constants.Tokens.ANNOTATE_ID

class DataStatement(context: ParserContext) : AbstractStatement(context) {
    override val syntaxType = SyntaxType.DATA
    private val components : ArrayList<AbstractStatement> = arrayListOf()

    override fun getComponents(): MutableList<AbstractStatement> {
        return components
    }

    private fun convertIdentifierToString(tokenStatement: TokenStatement) : InnerDefaultExpression {
        val name = tokenStatement.token.content
        val expression = InnerDefaultExpression(context)
        val newToken = Token(Tokens.STRING_START + name.substring(1), tokenStatement.token.location)
        expression.right = TokenStatement(newToken, context)
        return expression
    }

    override fun process(): CompilationExceptionsCollection {
        val errors = CompilationExceptionsCollection()
        try{
            while(transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.ANGLE_BRACKET_END_ID).removeWhen(WhenRemoveToken.Never)).isEmpty)
            {
                val newComp1 = TokenStatement(transmitter.expect(
                    ExpectParamsBuilder.tokenOf(Predicates.IS_IDENTIFIER)
                    .withErrorMessage(ErrorMessages.IDENTIFIER_EXPECTED_ERROR)),context)
                components.add(convertIdentifierToString(newComp1))

                transmitter.expect(ExpectParamsBuilder.tokenOf(ANNOTATE_ID))

                val newComp2 = InnerDefaultExpression(context)
                newComp2.process().throwFirstIfExists()
                components.add(newComp2)

                if (transmitter.expectMaybe(ExpectParamsBuilder.tokenOf(Tokens.SEPARATOR_ID)).isEmpty) break
            }
            location = transmitter.expect(ExpectParamsBuilder.tokenOf(Tokens.ANGLE_BRACKET_END_ID)).location
        }
        catch(ex : AbstractCompilationException)
        {
            errors.add(ex)
        }
        return errors
    }
}
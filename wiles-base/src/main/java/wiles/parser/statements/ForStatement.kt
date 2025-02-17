package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.WilesException
import wiles.shared.AbstractStatement
import wiles.shared.WilesExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens.IN_ID

class ForStatement(oldContext: ParserContext) : AbstractStatement(oldContext) {
    private var identifierStatement : TokenStatement? = null
    private lateinit var inToken : TokenStatement
    private var inExpression : DefaultExpression = DefaultExpression(context)
    private val codeBlock = CodeBlockStatement(context)

    override val syntaxType: SyntaxType
        get() = SyntaxType.FOR

    override fun getComponents(): MutableList<AbstractStatement> {
        val list = mutableListOf<AbstractStatement>(identifierStatement?:return mutableListOf())
        list.add(inToken)
        list.add(inExpression)
        list.add(codeBlock)
        return list
    }

    override fun process(): WilesExceptionsCollection {
        val exceptions = WilesExceptionsCollection()
        try
        {
            identifierStatement = TokenStatement(transmitter.expect(tokenOf(Predicates.IS_IDENTIFIER)
                .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)

            val maybeToken = transmitter.expect(tokenOf(IN_ID))
            inToken = TokenStatement(maybeToken,context)
            inExpression = DefaultExpression(context)
            exceptions.addAll(inExpression.process())

            exceptions.addAll(codeBlock.process())
        }
        catch (ex : WilesException){
            exceptions.add(ex)
        }
        return exceptions
    }
}
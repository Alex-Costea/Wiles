package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens.FROM_ID
import wiles.shared.constants.Tokens.IN_ID
import wiles.shared.constants.Tokens.TO_ID
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.AbstractCompilationException
import wiles.parser.statements.expressions.DefaultExpression

class ForStatement(oldContext: Context) : AbstractStatement(oldContext.setWithinLoop(true)) {
    private var identifierStatement : TokenStatement? = null
    private var inToken : TokenStatement? = null
    private var fromToken : TokenStatement? = null
    private var toToken : TokenStatement? = null
    private var inExpression : DefaultExpression? = null
    private var fromExpression : DefaultExpression? = null
    private var toExpression : DefaultExpression? = null
    private val codeBlock = CodeBlockStatement(context)

    override val type: SyntaxType
        get() = SyntaxType.FOR

    override fun getComponents(): MutableList<AbstractStatement> {
        val list = mutableListOf<AbstractStatement>(identifierStatement?:return mutableListOf())
        if(inExpression != null) {
            list.add(inToken!!)
            list.add(inExpression!!)
        }
        if(fromExpression != null) {
            list.add(fromToken!!)
            list.add(fromExpression!!)
        }
        if(toExpression != null) {
            list.add(toToken!!)
            list.add(toExpression!!)
        }
        list.add(codeBlock)
        return list
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        try
        {
            identifierStatement = TokenStatement(transmitter.expect(tokenOf(Predicates.IS_IDENTIFIER)
                .withErrorMessage(IDENTIFIER_EXPECTED_ERROR)),context)

            var maybeToken = transmitter.expectMaybe(tokenOf(IN_ID))
            if(maybeToken.isPresent)
            {
                inToken = TokenStatement(maybeToken.get(),context)
                inExpression = DefaultExpression(context)
                exceptions.addAll(inExpression!!.process())
            }

            maybeToken = transmitter.expectMaybe(tokenOf(FROM_ID))
            if(maybeToken.isPresent)
            {
                fromToken = TokenStatement(maybeToken.get(),context)
                fromExpression = DefaultExpression(context)
                exceptions.addAll(fromExpression!!.process())
            }

            maybeToken = transmitter.expectMaybe(tokenOf(TO_ID))
            if(maybeToken.isPresent)
            {
                toToken = TokenStatement(maybeToken.get(),context)
                toExpression = DefaultExpression(context)
                exceptions.addAll(toExpression!!.process())
            }

            exceptions.addAll(codeBlock.process())
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}
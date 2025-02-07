package wiles.parser.statements

import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.builders.ParserContext
import wiles.parser.statements.expressions.DefaultExpression
import wiles.shared.AbstractCompilationException
import wiles.shared.CompilationExceptionsCollection
import wiles.shared.SyntaxType
import wiles.shared.constants.ErrorMessages.IDENTIFIER_EXPECTED_ERROR
import wiles.shared.constants.Predicates
import wiles.shared.constants.Tokens.IN_ID

class ForStatement(oldContext: ParserContext) : AbstractStatement(oldContext.setWithinLoop(true)) {
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

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
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
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}
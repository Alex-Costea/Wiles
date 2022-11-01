package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.constants.ErrorMessages.INVALID_STATEMENT_ERROR
import `in`.costea.wiles.constants.Predicates.ANYTHING
import `in`.costea.wiles.constants.Tokens.CASE_ID
import `in`.costea.wiles.constants.Tokens.ELSE_ID
import `in`.costea.wiles.constants.Tokens.TERMINATOR_ID
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.exceptions.UnexpectedTokenException
import `in`.costea.wiles.statements.expressions.DefaultExpression

class WhenStatement(context: Context) : AbstractStatement(context) {

    private val branches : MutableList<Pair<AbstractStatement,CodeBlockStatement>> = mutableListOf()

    override val type: SyntaxType
        get() = SyntaxType.WHEN

    override fun getComponents(): List<AbstractStatement> {
        return branches.flatMap { (x, y) -> listOf(x, y) }
    }

    override fun handleEndOfStatement()
    {
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        var condition : AbstractStatement
        var blockStatement : CodeBlockStatement
        var isFirst = true
        try
        {
            var startsWithCase = true
            do
            {
                if(!isFirst)
                {
                    val tempToken = transmitter.expectMaybe(tokenOf(ELSE_ID))
                    if (tempToken.isPresent) {
                        condition = TokenStatement(tempToken.get(), context)
                        blockStatement = CodeBlockStatement(context)
                        exceptions.addAll(blockStatement.process())
                        transmitter.expectMaybe(tokenOf(TERMINATOR_ID).dontIgnoreNewLine())
                        branches.add(Pair(condition, blockStatement))
                        break
                    }
                }
                if(!startsWithCase)
                {
                    throw UnexpectedTokenException(INVALID_STATEMENT_ERROR,
                        transmitter.expect(tokenOf(ANYTHING).withErrorMessage(INVALID_STATEMENT_ERROR)).location)
                }
                if(!isFirst)
                    transmitter.expect(tokenOf(CASE_ID))
                else if(transmitter.expectMaybe(tokenOf(CASE_ID)).isEmpty)
                    startsWithCase = false
                isFirst = false
                condition = DefaultExpression(context)
                blockStatement = CodeBlockStatement(context)
                exceptions.addAll(condition.process())
                exceptions.addAll(blockStatement.process())
                branches.add(Pair(condition, blockStatement))
                transmitter.expectMaybe(tokenOf(TERMINATOR_ID).dontIgnoreNewLine())
            }
            while(transmitter.expectMaybe(tokenOf(CASE_ID).or(ELSE_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}
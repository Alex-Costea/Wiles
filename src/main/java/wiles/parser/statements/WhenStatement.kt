package wiles.parser.statements

import wiles.parser.builders.Context
import wiles.parser.builders.ExpectParamsBuilder.Companion.tokenOf
import wiles.parser.constants.ErrorMessages.INVALID_STATEMENT_ERROR
import wiles.parser.constants.Predicates.ANYTHING
import wiles.parser.constants.Tokens.CASE_ID
import wiles.parser.constants.Tokens.ELSE_ID
import wiles.parser.constants.Tokens.TERMINATOR_ID
import wiles.parser.data.CompilationExceptionsCollection
import wiles.parser.enums.SyntaxType
import wiles.parser.enums.WhenRemoveToken
import wiles.parser.exceptions.AbstractCompilationException
import wiles.parser.exceptions.UnexpectedTokenException

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
                condition = wiles.parser.statements.expressions.DefaultExpression(context)
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
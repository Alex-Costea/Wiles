package `in`.costea.wiles.statements

import `in`.costea.wiles.builders.Context
import `in`.costea.wiles.builders.ExpectParamsBuilder.Companion.tokenOf
import `in`.costea.wiles.constants.Tokens.CASE_ID
import `in`.costea.wiles.constants.Tokens.ELSE_ID
import `in`.costea.wiles.constants.Tokens.TERMINATOR_ID
import `in`.costea.wiles.data.CompilationExceptionsCollection
import `in`.costea.wiles.enums.SyntaxType
import `in`.costea.wiles.enums.WhenRemoveToken
import `in`.costea.wiles.exceptions.AbstractCompilationException
import `in`.costea.wiles.statements.expressions.DefaultExpression

class IfStatement(context: Context) : AbstractStatement(context) {

    private var handledEOL = true
    private val branches : MutableList<Pair<AbstractStatement,CodeBlockStatement>> = mutableListOf()

    override val type: SyntaxType
        get() = SyntaxType.WHEN

    override fun getComponents(): List<AbstractStatement> {
        return branches.flatMap { (x, y) -> listOf(x, y) }
    }

    override fun handleEndOfStatement()
    {
        if(!handledEOL)
            super.handleEndOfStatement()
    }

    override fun process(): CompilationExceptionsCollection {
        val exceptions = CompilationExceptionsCollection()
        var condition : AbstractStatement
        var blockStatement : CodeBlockStatement
        var isFirst = true
        try
        {
            do
            {
                val tempToken = transmitter.expectMaybe(tokenOf(ELSE_ID))
                if (tempToken.isPresent) {
                    condition = TokenStatement(tempToken.get(), context)
                    blockStatement = CodeBlockStatement(context)
                    exceptions.addAll(blockStatement.process())
                    handledEOL = false
                    branches.add(Pair(condition, blockStatement))
                    break
                }
                if(!isFirst)
                    transmitter.expect(tokenOf(CASE_ID))
                isFirst = false
                condition = DefaultExpression(context)
                blockStatement = CodeBlockStatement(context)
                exceptions.addAll(condition.process())
                exceptions.addAll(blockStatement.process())
                branches.add(Pair(condition, blockStatement))
                transmitter.expectMaybe(tokenOf(TERMINATOR_ID))
            }
            while(transmitter.expectMaybe(tokenOf(CASE_ID).or(ELSE_ID).removeWhen(WhenRemoveToken.Never)).isPresent)
        }
        catch (ex : AbstractCompilationException){
            exceptions.add(ex)
        }
        return exceptions
    }
}